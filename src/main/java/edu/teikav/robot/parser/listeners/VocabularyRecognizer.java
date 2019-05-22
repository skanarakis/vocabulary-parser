package edu.teikav.robot.parser.listeners;

import edu.teikav.robot.parser.domain.*;
import edu.teikav.robot.parser.exceptions.InvalidGraphException;
import edu.teikav.robot.parser.exceptions.NoMatchingVertexException;
import edu.teikav.robot.parser.exceptions.RecognizerProcessingException;
import edu.teikav.robot.parser.exceptions.UnrecognizedPublisherException;
import edu.teikav.robot.parser.services.InventoryService;
import edu.teikav.robot.parser.services.PublisherSpecificationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.xml.stream.XMLStreamException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
@Qualifier("VocabularyRecognizer")
public class VocabularyRecognizer extends TokenEmitter {

    private Logger logger = LoggerFactory.getLogger(VocabularyRecognizer.class);
    private static final String NEW_INVENTORY_ITEM_DEBUG_MESSAGE = "New InventoryItem object with value {}";
    private static final String SPACE = " ";

    private PublisherSpecificationRegistry registry;
    private PublisherSpecification activeSpec;

    private InventoryService inventoryService;

    private String activeVocabularyPart;
    private String previousVocabularyPart;
    private InventoryItem currentItem;

    private boolean isFirstStreamedToken = true;

    private StringBuilder cachedPart = new StringBuilder();

    @FunctionalInterface
    private interface ItemUpdateSnippet {
       void updateItem(PublisherSpecification context, InventoryItem item, String value);
    }

    private static Map<String, ItemUpdateSnippet> itemUpdateSnippets;

    static {
        itemUpdateSnippets = new HashMap<>();
        itemUpdateSnippets.put("GRAMMAR_TYPE", (grammarContext, item, value) -> {
            // inventoryItemGrammarType - get it from the grammar context
            Optional<SpeechPart> grammarType = grammarContext.getSpeechPartFor(value);
            item.setTermType(grammarType.orElseThrow(() -> new RuntimeException("Inconsistency " +
                    "when fetching Grammar Type for >>" + value + "<<")));
        });
        itemUpdateSnippets.put("TRANSLATION", (grammarContext, item, value) -> item.setTranslation(value));
        itemUpdateSnippets.put("PRONUNCIATION", (grammarContext, item, value) -> item.setPronunciation(value));
        itemUpdateSnippets.put("EXAMPLE", (grammarContext, item, value) -> item.setExample(value));
        itemUpdateSnippets.put("DERIVATIVES", (grammarContext, item, value) -> item.setDerivative(value));
        itemUpdateSnippets.put("VERB_PARTICIPLES", (grammarContext, item, value) -> item.setVerbParticiples(value));
    }

    @Autowired
    VocabularyRecognizer(PublisherSpecificationRegistry registry, InventoryService inventoryService,
                         @Qualifier("SecondPassOutputStream") OutputStream outputStream)
            throws XMLStreamException {

        super(outputStream);

        activeVocabularyPart = "TERM";
        currentToken = new VocabularyToken();
        this.inventoryService = inventoryService;
        this.registry = registry;
    }

    @Override
    public void reset() {
        logger.info("Resetting {}", this.getClass().getName());
        this.activeSpec = null;
        isFirstStreamedToken = true;
    }

    @Override
    public void processToken(String tokenStringValue)
    {
        logger.debug("----\tProcessing token {} - Active Vocabulary part : {}", tokenStringValue, activeVocabularyPart);

        if (this.activeSpec == null) {
            createRecognitionEnvironment();
        }
        if (isFirstStreamedToken) {
            handleFirstStreamedToken(tokenStringValue);
            return;
        }
        doProcessToken(tokenStringValue);
    }

    private void createRecognitionEnvironment() {
        // Recognizer depends on an active Publisher Registry grammar for its job
        // In case no active grammar isInventoried in the Publisher Grammar Registry, we fail
        Optional<PublisherSpecification> optSpec = this.registry.getActiveSpec();
        activeSpec = optSpec.orElseThrow(UnrecognizedPublisherException::new);

        if (!activeSpec.containsRootVocabularyToken("TERM")) {
            throw new InvalidGraphException("Grammar Graph must have a TERM vertex");
        }
        // Begin from the vertex showing the vocabulary term
        activeVocabularyPart = "TERM";
    }

    private String findNextVocabularyPart(String tokenStringValue) {
        List<String> nextVocabularyParts = activeSpec.getValidTransitionsFor(activeVocabularyPart);
        logger.debug("Valid transitions for {} : {}", activeVocabularyPart, nextVocabularyParts);
        if (nextVocabularyParts.size() == 1) {
            String newPart = nextVocabularyParts.get(0);
            logger.debug("New Vertex is {}", newPart);
            return newPart;
        }
        for (String newPart : nextVocabularyParts) {
            Pattern pattern = Pattern.compile(activeSpec.getTermPattern(newPart));
            if (pattern.matcher(tokenStringValue).matches()) {
                logger.debug("Matched pattern {} for token {} (:{}). New Vertex is {}",
                        activeSpec.getTermPattern(newPart), tokenStringValue, newPart, newPart);
                return newPart;
            } else {
                logger.debug("No match for pattern {} for token {} (:{})", activeSpec.getTermPattern(newPart),
                        tokenStringValue, newPart);
            }
        }
        logger.debug("No new Vertex. Keep the same {}", activeVocabularyPart);
        return activeVocabularyPart;
    }

    private void handleFirstStreamedToken(String tokenStringValue) {
        if (activeSpec.isTermPotentiallySplit("TERM")) {
            logger.debug("Caching TERM segment {}", tokenStringValue);
            cachedPart.append(SPACE).append(tokenStringValue);
        } else {
            currentItem = new InventoryItem(tokenStringValue);
            logger.debug(NEW_INVENTORY_ITEM_DEBUG_MESSAGE, tokenStringValue);
        }
        isFirstStreamedToken = false;
    }

    private void doProcessToken(String tokenStringValue) {
        previousVocabularyPart = activeVocabularyPart;
        activeVocabularyPart = findNextVocabularyPart(tokenStringValue);
        if (previousVocabularyPart.equals(activeVocabularyPart)) {
            handleReEntrantVertexTransition(tokenStringValue);
        } else {
            handleNormalVertexTransition(tokenStringValue);
        }
        checkForPotentiallyLastPart();
    }

    private void handleReEntrantVertexTransition(String tokenStringValue) {
        if (activeSpec.isTermPotentiallySplit(activeVocabularyPart)) {
            cachedPart.append(SPACE).append(tokenStringValue);
            logger.debug("{} is split. Caching current segment {}. Complete cache is '{}'",
                    activeVocabularyPart, tokenStringValue, cachedPart.toString());
        } else {
            throw new NoMatchingVertexException("No matching vertex found and active vertex cannot hold partial segments");
        }
    }

    private void handleNormalVertexTransition(String tokenStringValue) {
        if (!cachedPart.toString().isEmpty()) {
            handleCachedData();
        }
        if (activeSpec.isTermPotentiallySplit(activeVocabularyPart)) {
            cachedPart.append(SPACE).append(tokenStringValue);
            logger.debug("{} might be split. Caching current segment {}. Complete cache is '{}'",
                    activeVocabularyPart, tokenStringValue, cachedPart.toString());
        } else {
            processVocabularyPart(tokenStringValue);
        }
    }

    private void processVocabularyPart(String tokenStringValue) {
        logger.debug("*** Entered PROCESS with activeVocabularyPart: {}, previousVocabularyPart: {}, cache: '{}'",
                activeVocabularyPart, previousVocabularyPart, cachedPart.toString());

        currentToken.setTokenType(VocabularyTokenType.valueOf(activeVocabularyPart));

        if (activeVocabularyPart.equals("TERM")) {
            currentItem = new InventoryItem(tokenStringValue);
            logger.debug(NEW_INVENTORY_ITEM_DEBUG_MESSAGE, tokenStringValue);
        } else {
            ItemUpdateSnippet snippet = itemUpdateSnippets.get(activeVocabularyPart);
            if (activeSpec.isTermPotentiallyComposite(activeVocabularyPart)) {
                String compositePartSplitToken = activeSpec.getCompositePartSplitToken(activeVocabularyPart);
                String[] partsOfComposite = tokenStringValue.split(compositePartSplitToken);
                if (partsOfComposite.length == 1) {
                    // Not composite
                    snippet.updateItem(activeSpec, currentItem, tokenStringValue);
                } else {
                    processCompositeVocabularyPart(partsOfComposite);
                }
            } else {
                snippet.updateItem(activeSpec, currentItem, tokenStringValue);
            }
        }
        cachedPart.delete(0, cachedPart.length());
    }

    private void handleCachedData() {
        String cachedPartString = cachedPart.toString().trim();
        logger.debug("Time to handle cache {} for ({})", cachedPartString, previousVocabularyPart);
        if (previousVocabularyPart.equals("TERM")) {
            currentItem = new InventoryItem(cachedPartString);
            logger.debug(NEW_INVENTORY_ITEM_DEBUG_MESSAGE, cachedPartString);
        } else {
            ItemUpdateSnippet snippet = itemUpdateSnippets.get(previousVocabularyPart);
            snippet.updateItem(activeSpec, currentItem, cachedPartString);
            logger.debug("Updating InventoryItem object with value {}", cachedPartString);
        }
        cachedPart.delete(0, cachedPart.length());
    }

    private void processCompositeVocabularyPart(String[] compositeParts) {
        logger.debug("Entered composite section for {}", activeVocabularyPart);
        List<String> compositePartTypes = activeSpec.getCompositePartsFor(activeVocabularyPart);

        if (compositeParts.length != compositePartTypes.size()) {
            throw new RecognizerProcessingException("Segments of a composite vocabulary part must have corresponding split token segments");
        }

        for (int i = 0; i < compositePartTypes.size(); i++) {
            logger.debug("Iterating for segment {}({})", compositeParts[i], compositePartTypes.get(i));
            ItemUpdateSnippet snippet = itemUpdateSnippets.get(compositePartTypes.get(i));
            snippet.updateItem(activeSpec, currentItem, compositeParts[i]);
        }
    }

    private void checkForPotentiallyLastPart() {
        if (activeSpec.isTermPotentiallyLast(activeVocabularyPart)) {
            inventoryService.save(currentItem);
            logger.info("Inventory saving for {}", currentItem);
        }
    }


}
