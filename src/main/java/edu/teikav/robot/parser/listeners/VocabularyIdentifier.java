package edu.teikav.robot.parser.listeners;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import edu.teikav.robot.parser.domain.PublisherGrammarContext;
import edu.teikav.robot.parser.domain.VocabularyToken;
import edu.teikav.robot.parser.services.PublisherGrammarRegistry;

@Component
@Qualifier("FirstPassParser")
public class VocabularyIdentifier extends AbstractRTFCommandsCallbackProcessor {

    private Logger logger = LoggerFactory.getLogger(AbstractRTFCommandsCallbackProcessor.class);

    private boolean taskCompleted = false;

    private PublisherGrammarRegistry registry;
    private List<VocabularyToken> tokens;

    VocabularyIdentifier(PublisherGrammarRegistry registry,
                                @Qualifier("FirstPassOutputStream") OutputStream outputStream)
            throws XMLStreamException {
        super(outputStream);
        this.registry = registry;
        this.tokens = new ArrayList<>();
    }

    @Override
    public void processToken(String tokenString) {

        // Delegate for XML output
        rtfDumpListener.processString(tokenString);

        // In this first pass we only care for matching a grammar in the Registry
        // So, for every token that is being parsed, we add it in our internal list of tokens
        // and then re-calculate their combined hash-code.
        // Hash-code of each token cares only about its formatting (Bold/Italicized/Font-Size/Color)
        // Hopefully, at some time where we have parsed a complete vocabulary term (we do not
        // know where a vocabulary term ends without a grammar) having its constituent tokens,
        // we will have a hash-code that matches the hash-code of a grammar that has been properly
        // registered in the Registry. At that point, our work is done and we can let the parsing
        // continue just to output the XML file for debugging reasons.
        if (!taskCompleted) {
            logger.debug("Current Token is \n{}", currentToken);

            // Save token to the internal list of tokens participating in the hashing
            // Other token fields have been populated in the parent abstract listener class
            currentToken.setTokenString(tokenString.trim());
            tokens.add(currentToken.clone());

            // Calculate new hash code
            int vocabularyTermHashCode = Objects.hash(tokens);
            logger.debug("Hash Code calculated : {}", vocabularyTermHashCode);
            logger.debug("Tokens involved in hash code calculation : \n{}", tokens);

            // Ask Registry if there is any match
            Optional<PublisherGrammarContext> grammarContextOptional = registry.findGrammar(vocabularyTermHashCode);
            if (grammarContextOptional.isPresent()) {
                // Set the current active grammar and end task
                registry.setActiveGrammarContext(grammarContextOptional.get());
                taskCompleted = true;
            }
        }
    }

    @Override
    public void reset() {

        // Resetting the colors with a call to super.reset()
        super.reset();

        logger.info("Resetting {}", this.getClass().getName());
        taskCompleted = false;
        tokens.clear();
    }
}