package edu.teikav.robot.parser.listeners;

import edu.teikav.robot.parser.domain.*;
import edu.teikav.robot.parser.exceptions.UnknownGrammarException;
import edu.teikav.robot.parser.services.InventoryService;
import edu.teikav.robot.parser.services.PublisherGrammarRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.xml.stream.XMLStreamException;
import java.io.OutputStream;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

@Component
@Qualifier("SecondPassParser")
public class VocabularySemanticsTokenizer extends AbstractRTFCommandsCallbackProcessor {

    private static final String DELIMITER_RFT_TOKEN_DENOTING_VOCABULARY_START = "Riched20";
    private Logger logger = LoggerFactory.getLogger(VocabularySemanticsTokenizer.class);

    // Flag to by-pass the RFT meta-data and focus only on actual vocabulary tokens
    private boolean signalBeginningOfVocabulary = false;

    // Utilize policy rules of active grammar
    private PublisherGrammarRegistry registry;
    private PublisherGrammarContext grammarContext;
    private List<String> vocabularyParts;
    private ListIterator<String> vocabularyPartsIterator;

    private InventoryService inventoryService;

    // Placeholder variables to hold token attributes for every parsed token
    private String inventoryItemTerm;
    private String inventoryItemTranslation;
    private TermGrammarTypes inventoryItemGrammarType;
    private String inventoryItemExample;
    private String inventoryItemPronunciation;

    public VocabularySemanticsTokenizer(PublisherGrammarRegistry registry, InventoryService inventoryService,
                                        @Qualifier("SecondPassOutputStream") OutputStream outputStream)
            throws XMLStreamException {

        super(outputStream);

        currentToken = new VocabularyToken();
        this.inventoryService = inventoryService;
        this.registry = registry;
    }

    @Override
    public void reset() {
        logger.info("Resetting {}", this.getClass().getName());
        signalBeginningOfVocabulary = false;
        this.grammarContext = null;
    }

    @Override
    public void processToken(String tokenString)
    {

        // Delegate for XML output
        rtfDumpListener.processString(tokenString);

        // The very first invocation of processString method will lead to
        // initialization of the necessary Grammar context. Without it, we
        // cannot complete our work. So, initialize will throw an exception
        // in case no active grammar exists in the Publisher Grammar Registry
        if (this.grammarContext == null) {
            initialize();
        }

        // If we are in state of accepting vocabulary tokens
        if (signalBeginningOfVocabulary) {

            if (vocabularyPartsIterator.hasNext()) {
                logger.debug("Processing vocabulary token >>{}<<", tokenString);
                currentToken.setTokenString(tokenString.trim());

                // Take the next part of the vocabulary
                String vocabularyPart = vocabularyPartsIterator.next();
                VocabularyTokenType tokenType = VocabularyTokenType.valueOf(vocabularyPart);
                currentToken.setTokenType(tokenType);
                switch (tokenType) {
                    case GARBAGE:
                        // Do nothing for Garbage tokens, we do not need them saved in the inventory
                        break;
                    case TERM:
                        inventoryItemTerm = currentToken.getTokenString();
                        break;
                    case GRAMMAR_TYPE:
                        // inventoryItemGrammarType - get it from the grammar context
                        Optional<TermGrammarTypes> grammarType = grammarContext.getGrammarTypeFor(currentToken.getTokenString());
                        inventoryItemGrammarType = grammarType.orElseThrow(() -> new RuntimeException("Inconsistency " +
                                "when fetching Grammar Type for >>" + currentToken.getTokenString() + "<<"));
                        break;
                    case PRONUNCIATION:
                        inventoryItemPronunciation = currentToken.getTokenString();
                        break;
                    case TRANSLATION:
                        inventoryItemTranslation = currentToken.getTokenString();
                        break;
                    case EXAMPLE:
                        inventoryItemExample = currentToken.getTokenString();
                        break;
                    case ANTONYMS:
                    case SYNONYMS:
                        break;
                }
            }

            if (!vocabularyPartsIterator.hasNext()) {
                // We have parsed a complete vocabulary term
                // Reset List iterator to point to the beginning of the list
                // so that we can take the next term
                while (vocabularyPartsIterator.hasPrevious()) {
                    vocabularyPartsIterator.previous(); // No need to save the returned value, we just need to reset iterator
                }

                // Save the inventory item
                InventoryItem newItem = new InventoryItem(inventoryItemTerm);
                newItem.setTermType(inventoryItemGrammarType);
                newItem.setTranslation(inventoryItemTranslation);
                newItem.setExample(inventoryItemExample);
                newItem.setPronunciation(inventoryItemPronunciation);

                inventoryService.saveNewInventoryItem(newItem);
            }

        }

        // Check for the RFT parser delimiter
        if (tokenString.startsWith(DELIMITER_RFT_TOKEN_DENOTING_VOCABULARY_START)) {
            signalBeginningOfVocabulary = true;
        }
    }

    private void initialize() {
        Optional<PublisherGrammarContext> grammarContextOptional = this.registry.getActiveGrammarContext();
        grammarContext = grammarContextOptional.orElseThrow(UnknownGrammarException::new);

        vocabularyParts  = grammarContext.vocabularyOrdering();
        logger.info("Active Grammar has the following Vocabulary Terms Structure: \n\t{}", vocabularyParts);
        vocabularyPartsIterator = vocabularyParts.listIterator();
    }
}
