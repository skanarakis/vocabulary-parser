package edu.teikav.robot.parser.listeners;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.stream.XMLStreamException;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import edu.teikav.robot.parser.domain.InventoryItem;
import edu.teikav.robot.parser.domain.PublisherGrammarContext;
import edu.teikav.robot.parser.domain.TermGrammarTypes;
import edu.teikav.robot.parser.domain.VocabularyToken;
import edu.teikav.robot.parser.domain.VocabularyTokenType;
import edu.teikav.robot.parser.exceptions.InvalidGrammarGraphException;
import edu.teikav.robot.parser.exceptions.NoMatchingVertexException;
import edu.teikav.robot.parser.exceptions.RecognizerProcessingException;
import edu.teikav.robot.parser.exceptions.UnknownGrammarException;
import edu.teikav.robot.parser.services.InventoryService;
import edu.teikav.robot.parser.services.PublisherGrammarRegistry;

@Component
@Qualifier("SecondPassParser")
public class VocabularyRecognizer extends AbstractRTFCommandsCallbackProcessor {

    private Logger logger = LoggerFactory.getLogger(VocabularyRecognizer.class);
    private static final String NEW_INVENTORY_ITEM_DEBUG_MESSAGE = "New InventoryItem object with value {}";
    private static final String SPACE = " ";

    private PublisherGrammarRegistry registry;
    private PublisherGrammarContext grammarContext;

    private InventoryService inventoryService;

    private DefaultDirectedGraph<String, DefaultEdge> graph;
    private String activeVocabularyPart;
    private String previousVocabularyPart;
    private InventoryItem currentItem;

    private boolean isFirstStreamedToken = true;

    private StringBuilder cachedPart = new StringBuilder();

    @FunctionalInterface
    private interface ItemUpdateSnippet {
       void updateItem(PublisherGrammarContext context, InventoryItem item, String value);
    }

    private static Map<String, ItemUpdateSnippet> itemUpdateSnippets;

    static {
        itemUpdateSnippets = new HashMap<>();
        itemUpdateSnippets.put("GRAMMAR_TYPE", (grammarContext, item, value) -> {
            // inventoryItemGrammarType - get it from the grammar context
            Optional<TermGrammarTypes> grammarType = grammarContext.getGrammarTypeFor(value);
            item.setTermType(grammarType.orElseThrow(() -> new RuntimeException("Inconsistency " +
                    "when fetching Grammar Type for >>" + value + "<<")));
        });
        itemUpdateSnippets.put("TRANSLATION", (grammarContext, item, value) -> item.setTranslation(value));
        itemUpdateSnippets.put("EXAMPLE", (grammarContext, item, value) -> item.setExample(value));
        itemUpdateSnippets.put("DERIVATIVES", (grammarContext, item, value) -> item.setDerivative(value));
        itemUpdateSnippets.put("VERB_PARTICIPLES", (grammarContext, item, value) -> item.setVerbParticiples(value));
    }

    VocabularyRecognizer(PublisherGrammarRegistry registry, InventoryService inventoryService,
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
        this.grammarContext = null;
    }

    @Override
    public void processToken(String tokenStringValue)
    {
        logger.debug("----\tProcessing token {} - Active Vocabulary part : {}", tokenStringValue, activeVocabularyPart);

        if (this.grammarContext == null) {
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
        // In case no active grammar exists in the Publisher Grammar Registry, we fail
        Optional<PublisherGrammarContext> grammarContextOptional = this.registry.getActiveGrammarContext();
        grammarContext = grammarContextOptional.orElseThrow(UnknownGrammarException::new);
        List<String> vocabularyParts = grammarContext.vocabularyOrdering();
        logger.trace("Active Grammar has the following Vocabulary Terms Structure: \n\t{}", vocabularyParts);

        constructGraph(vocabularyParts);
        markRootVertex();
    }

    private void constructGraph(List<String> vocabularyParts) {
        // Construct the Graph
        graph = new DefaultDirectedGraph<>(DefaultEdge.class);

        vocabularyParts.forEach(graph::addVertex);

        List<String> relations = grammarContext.getStructureRelations();
        for (String relation : relations) {
            String[] vertices = relation.split("->");
            graph.addEdge(vertices[0].trim(), vertices[1].trim());
        }
    }

    private void markRootVertex() {
        // Begin from the vertex showing the vocabulary term
        Optional<String> optionalTermVertex = graph.vertexSet().stream()
                .filter(v -> v.equals("TERM")).findAny();
        if (optionalTermVertex.isPresent()){
            activeVocabularyPart = optionalTermVertex.get();
        } else {
            throw new InvalidGrammarGraphException("Grammar Graph must have a TERM vertex");
        }
    }

    private void handleFirstStreamedToken(String tokenStringValue) {
        if (grammarContext.isPartPotentiallySplit("TERM")) {
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
        activeVocabularyPart = findNextVertexInVocabularyPartsGraph(tokenStringValue);
        if (previousVocabularyPart.equals(activeVocabularyPart)) {
            handleReEntrantVertexTransition(tokenStringValue);
        } else {
            handleNormalVertexTransition(tokenStringValue);
        }
        checkForPotentiallyLastPart();
    }

    private void handleReEntrantVertexTransition(String tokenStringValue) {
        if (grammarContext.isPartPotentiallySplit(activeVocabularyPart)) {
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
        if (grammarContext.isPartPotentiallySplit(activeVocabularyPart)) {
            cachedPart.append(SPACE).append(tokenStringValue);
            logger.debug("{} might be split. Caching current segment {}. Complete cache is '{}'",
                    activeVocabularyPart, tokenStringValue, cachedPart.toString());
        } else {
            processVocabularyPart(tokenStringValue);
        }
    }

    private String findNextVertexInVocabularyPartsGraph(String tokenStringValue) {
        Set<DefaultEdge> edges = graph.outgoingEdgesOf(activeVocabularyPart);
        for (DefaultEdge edge : edges) {
            if (edges.size() == 1) {
                String newVertex = graph.getEdgeTarget(edge);
                logger.debug("New Vertex is {}", newVertex);
                return newVertex;
            }
            String relatedVertex = graph.getEdgeTarget(edge);
            Pattern pattern = Pattern.compile(grammarContext.patternOfToken(relatedVertex));
            if (pattern.matcher(tokenStringValue).matches()) {
                logger.debug("Matched pattern {} for token {} (:{}). New Vertex is {}",
                        grammarContext.patternOfToken(relatedVertex), tokenStringValue, relatedVertex, relatedVertex);
                return relatedVertex;
            } else {
                logger.debug("No match for pattern {} for token {} (:{})", grammarContext.patternOfToken(relatedVertex), tokenStringValue,
                        relatedVertex);
            }
        }
        logger.debug("No new Vertex. Keep the same {}", activeVocabularyPart);
        return activeVocabularyPart;
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
            if (grammarContext.isPartPotentiallyComposite(activeVocabularyPart)) {
                String compositePartSplitToken = grammarContext.getCompositePartSplitToken(activeVocabularyPart);
                String[] partsOfComposite = tokenStringValue.split(compositePartSplitToken);
                if (partsOfComposite.length == 1) {
                    // Not composite
                    snippet.updateItem(grammarContext, currentItem, tokenStringValue);
                } else {
                    processCompositeVocabularyPart(partsOfComposite);
                }
            } else {
                snippet.updateItem(grammarContext, currentItem, tokenStringValue);
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
            snippet.updateItem(grammarContext, currentItem, cachedPartString);
            logger.debug("Updating InventoryItem object with value {}", cachedPartString);
        }
        cachedPart.delete(0, cachedPart.length());
    }

    private void processCompositeVocabularyPart(String[] compositeParts) {
        logger.debug("Entered composite section for {}", activeVocabularyPart);
        List<String> compositePartTypes = grammarContext.getCompositePartsFor(activeVocabularyPart);

        if (compositeParts.length != compositePartTypes.size()) {
            throw new RecognizerProcessingException("Segments of a composite vocabulary part must have corresponding split token segments");
        }

        for (int i = 0; i < compositePartTypes.size(); i++) {
            logger.debug("Iterating for segment {}({})", compositeParts[i], compositePartTypes.get(i));
            ItemUpdateSnippet snippet = itemUpdateSnippets.get(compositePartTypes.get(i));
            snippet.updateItem(grammarContext, currentItem, compositeParts[i]);
        }
    }

    private void checkForPotentiallyLastPart() {
        if (grammarContext.isPartPotentiallyLast(activeVocabularyPart)) {

            inventoryService.saveNewInventoryItem(currentItem);
            logger.info("Inventory saving for {}", currentItem);
        }
    }
}
