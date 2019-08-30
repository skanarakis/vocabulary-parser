package edu.teikav.robot.parser.processors;

import edu.teikav.robot.parser.domain.InventoryItem;
import edu.teikav.robot.parser.domain.PublisherSpecification;
import edu.teikav.robot.parser.domain.SpeechPart;
import edu.teikav.robot.parser.exceptions.InvalidGraphException;
import edu.teikav.robot.parser.exceptions.NoMatchingVertexException;
import edu.teikav.robot.parser.exceptions.UnrecognizedPublisherException;
import edu.teikav.robot.parser.services.InventoryService;
import edu.teikav.robot.parser.services.PublisherSpecificationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VocabularyRecognizer {

    private Logger logger = LoggerFactory.getLogger(VocabularyRecognizer.class);
    private static final String NEW_INVENTORY_ITEM_DEBUG_MESSAGE = "Constructing new inventory item object with value [{}]";
    private static final String SPACE = " ";

    private PublisherSpecificationRegistry registry;
    private PublisherSpecification activeSpec;

    private InventoryService inventoryService;

    private String activeVocabularyPart;
    private String previousVocabularyPart;
    private InventoryItem currentItem;

    private boolean isFirstStreamedToken = true;

    private StringBuilder cachedPart = new StringBuilder();
    private List<String> matchedTransitions;

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
        itemUpdateSnippets.put("TRANSLATION", (grammarContext, item, value) -> {
            if (StringUtils.isEmpty(item.getTranslation())) {
                item.setTranslation(value);
            } else {
                item.setTranslation(item.getTranslation() + "##" + value);
            }
        });
        itemUpdateSnippets.put("PRONUNCIATION", (grammarContext, item, value) -> {
            value = value.replace("[", "");
            value = value.replace("]", "");
            item.setPronunciation(value);
        });
        itemUpdateSnippets.put("EXAMPLE", (grammarContext, item, value) -> {
            value = value.replace("\"", "");
            item.setExample(value);
        });
        itemUpdateSnippets.put("DERIVATIVES", (grammarContext, item, value) -> item.setDerivative(value));
        itemUpdateSnippets.put("OPPOSITES", (grammarContext, item, value) -> item.setOpposite(value));
        itemUpdateSnippets.put("PHRASE", (grammarContext, item, value) -> item.setPhrase(value));
        itemUpdateSnippets.put("SYNONYMS", (grammarContext, item, value) -> item.setSynonyms(value));
        itemUpdateSnippets.put("VERB_PARTICIPLES", (grammarContext, item, value) -> {
            // TODO: Short-term correction to erase the last parenthesis left after composite part processing
            // TODO: This is not generic and should be moved elsewhere. Find a smarter way to do it
            if (!value.contains("(") && value.contains(")")) {
                value = value.replace(")", "");
            }
            item.setVerbParticiples(value);
        });
    }

    public VocabularyRecognizer(PublisherSpecificationRegistry registry, InventoryService inventoryService) {
        activeVocabularyPart = "TERM";
        this.inventoryService = inventoryService;
        this.registry = registry;
        this.matchedTransitions = new ArrayList<>();
    }

    public void recognizeVocabulary(Stream<String> vocabularyPartsStream) {
        vocabularyPartsStream.forEach(this::processToken);
    }

    private void processToken(String tokenStringValue) {
        logger.debug("\n*********\nProcessing token [{}]\nActive Vocabulary part : {}\n*********",
                tokenStringValue, activeVocabularyPart);

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

        if (activeSpec.getPublisher() != null) {
            logger.info("Found Publisher with name '{}'", activeSpec.getPublisher().getName());
        }

        // TODO: Move this validation when registering the graph with publisher specs
        if (!activeSpec.containsRootVocabularyToken("TERM")) {
            throw new InvalidGraphException("Grammar Graph must have a TERM vertex");
        }
        // Begin from the vertex showing the vocabulary term
        activeVocabularyPart = "TERM";
    }

    private void handleFirstStreamedToken(String tokenStringValue) {
        if (activeSpec.isTermPotentiallySplit("TERM")) {
            logger.debug("Caching TERM segment {}", tokenStringValue);
            cachedPart.append(tokenStringValue);
        } else {
            currentItem = InventoryItem.createEmptyItemFor(tokenStringValue);
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
        checkForPotentiallyLastPart(activeVocabularyPart);
    }

    private String findNextVocabularyPart(String tokenStringValue) {
        List<String> validTransitions = activeSpec.getValidTransitionsFor(activeVocabularyPart);
        logger.debug("Valid transitions for {} : {}", activeVocabularyPart, validTransitions);

        if (validTransitions.size() == 1) {
            String transition = validTransitions.get(0);
            if (activeSpec.getTermPattern(transition) == null || patternFound(tokenStringValue, transition)) {
                // In case next transition lacks RegEx pattern
                logger.debug("Next vocabulary part found: {}", transition);
                return transition;
            }
        } else {
            matchedTransitions.clear();
            for (String transition : validTransitions) {
                if (patternFound(tokenStringValue, transition)) {
                    if (transition.equals("TERM") && tokenFoundInCurrentItem(tokenStringValue)) {
                        logger.debug("Potential transition to TERM but the token content [{}] seems relevant with [{}]."
                                + " Ignore transition to a new TERM", tokenStringValue, currentItem.getTerm());
                    } else if (transition.equals("PHRASE") && !tokenFoundInCurrentItem(tokenStringValue)) {
                        logger.debug("Potential transition to PHRASE but the token content [{}] seems irrelevant with [{}]."
                                + " Ignore transition to PHRASE", tokenStringValue, currentItem.getTerm());
                    }
                    else {
                        matchedTransitions.add(transition);
                    }
                }
            }
            if (matchedTransitions.size() > 0) {
                if (matchedTransitions.size() == 1) {
                    return matchedTransitions.get(0);
                } else {
                    return resolvePatternMatchConflict(tokenStringValue, matchedTransitions);
                }
            }
        }
        logger.debug("No new vocabulary part found. Keep the same: {}", activeVocabularyPart);
        return activeVocabularyPart;
    }

    private boolean tokenFoundInCurrentItem(String tokenStringValue) {
        return tokenStringValue.contains(currentItem.getTerm()) ||
                (currentItem.getTranslation()!= null && currentItem.getTranslation().contains(tokenStringValue));
    }

    private String resolvePatternMatchConflict(String token, List<String> matchedTransitions) {
        int numberOfWords = token.split(" ").length;

        long rulesMatched =
                matchedTransitions.stream()
                        .filter(t -> activeSpec.getMaxWordsFor(t) >= numberOfWords &&
                                activeSpec.getMinWordsFor(t) <= numberOfWords).count();

        if (rulesMatched > 1) {
            throw new RuntimeException(String.format("Ambiguity for next transition between %s", matchedTransitions));
        }

        return matchedTransitions.stream()
                .filter(t -> activeSpec.getMaxWordsFor(t) >= numberOfWords &&
                        activeSpec.getMinWordsFor(t) <= numberOfWords)
                .findFirst().orElseThrow(() -> new RuntimeException(String.format("Impossible: %s", matchedTransitions)));
    }

    private boolean patternFound(String tokenStringValue, String nextPart) {
        Pattern pattern = Pattern.compile(activeSpec.getTermPattern(nextPart));
        if (pattern.matcher(tokenStringValue).matches()) {
            logger.debug("Matched pattern [{}] for token [{}]. Matched vocabulary part: [{}]",
                    activeSpec.getTermPattern(nextPart), tokenStringValue, nextPart);
            return true;
        } else {
            logger.debug("No match for pattern [{}] for token [{}] (Checking transition [{}])",
                    activeSpec.getTermPattern(nextPart), tokenStringValue, nextPart);
        }
        return false;
    }


    private void handleReEntrantVertexTransition(String tokenStringValue) {

        if (!activeSpec.isTermPotentiallySplit(activeVocabularyPart)) {
            throw new NoMatchingVertexException(
                    String.format("No matching next vocabulary part found and specs for [%s] indicate it cannot be split",
                            activeVocabularyPart));
        }

        cachedPart.append(SPACE).append(tokenStringValue);
        logger.debug("{} is split. Caching current segment {}. Complete cache is '{}'",
                    activeVocabularyPart, tokenStringValue, cachedPart.toString());
    }

    private void handleNormalVertexTransition(String tokenStringValue) {
        if (!cachedPart.toString().isEmpty()) {
            handleCachedData();
        }
        if (activeSpec.isTermPotentiallySplit(activeVocabularyPart)) {
            cachedPart.append(tokenStringValue);
            logger.debug("[{}] might be split. Caching current segment [{}]. Complete cache is [{}]",
                    activeVocabularyPart, tokenStringValue, cachedPart.toString());
        } else {
            processVocabularyPart(tokenStringValue);
        }
    }

    private void processVocabularyPart(String tokenStringValue) {

        if (activeVocabularyPart.equals("TERM")) {
            currentItem = InventoryItem.createEmptyItemFor(tokenStringValue);
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
    }

    private void handleCachedData() {
        String cachedPartString = cachedPart.toString().trim();
        logger.debug("Time to handle cache [{}] for [{}]", cachedPartString, previousVocabularyPart);
        if (previousVocabularyPart.equals("TERM")) {
            currentItem = InventoryItem.createEmptyItemFor(cachedPartString);
            logger.debug(NEW_INVENTORY_ITEM_DEBUG_MESSAGE, cachedPartString);
        } else {
            ItemUpdateSnippet snippet = itemUpdateSnippets.get(previousVocabularyPart);
            snippet.updateItem(activeSpec, currentItem, cachedPartString);
            logger.debug("Updating InventoryItem [{}] with value {}", currentItem.getTerm(), cachedPartString);
            checkForPotentiallyLastPart(previousVocabularyPart);
        }
        cachedPart.delete(0, cachedPart.length());
    }

    private void processCompositeVocabularyPart(String[] compositeParts) {
        logger.debug("Entered composite section for {}", activeVocabularyPart);
        List<String> compositePartTypes = activeSpec.getCompositePartsFor(activeVocabularyPart);

        // Remove empty strings and trim the other ones
        List<String> parts = Arrays.stream(compositeParts)
                .filter(part -> !part.equals(""))
                .map(String::trim)
                .collect(Collectors.toList());
        if (parts.size() != compositePartTypes.size()) {
            //throw new RecognizerProcessingException("Segments of a composite vocabulary part must have corresponding split token segments");
            // TODO: Handle Composite segments absence
            logger.warn("TODO: HANDLE SITUATION WHERE NOT ALL PARTS OF COMPOSITES ARE PRESENT");
        } else {
            for (int i = 0; i < compositePartTypes.size(); i++) {
                logger.debug("Iterating for segment >{}< ({})", parts.get(i), compositePartTypes.get(i));
                ItemUpdateSnippet snippet = itemUpdateSnippets.get(compositePartTypes.get(i));
                snippet.updateItem(activeSpec, currentItem, parts.get(i));
            }
        }
    }

    private void checkForPotentiallyLastPart(String part) {
        if (activeSpec.isTermPotentiallyLast(part)) {
            inventoryService.save(currentItem);
        }
    }
}
