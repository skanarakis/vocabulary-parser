package edu.teikav.robot.parser.listeners;

import edu.teikav.robot.parser.domain.PublisherSpecification;
import edu.teikav.robot.parser.domain.VocabularyToken;
import edu.teikav.robot.parser.services.PublisherSpecificationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.xml.stream.XMLStreamException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@Qualifier("PublisherIdentifier")
public class PublisherIdentifier extends TokenEmitter {

    private Logger logger = LoggerFactory.getLogger(PublisherIdentifier.class);

    private boolean taskCompleted = false;

    private PublisherSpecificationRegistry registry;
    private List<VocabularyToken> accumulatedTokens;

    @Autowired
    PublisherIdentifier(PublisherSpecificationRegistry registry,
                        @Qualifier("FirstPassOutputStream") OutputStream outputStream) throws XMLStreamException {
        super(outputStream);
        this.registry = registry;
        this.accumulatedTokens = new ArrayList<>();
    }

    @Override
    public void processToken(final String tokenString) {

        // In the first pass we attempt to identify a publisher in the Registry
        // So, for every token that is being parsed, we add it in our internal list of tokens
        // and then re-calculate their combined hash-code.
        // Hash-code of each token is calculated based on their formatting
        // (Bold/Italicized/Font-Size/Color) and language

        // Hopefully, at some time where we have parsed a complete vocabulary term (we do not
        // know where a vocabulary term ends without knowing the publisher context)
        // we will have a hash-code that matches any of the hash-codes stored for publishers in Registry
        // At that point, our work is done (we have identified a publisher) and we can let the parsing continue
        // just to output the XML file for debugging reasons.
        if (!taskCompleted) {
            logger.debug("Current Token is \n{}", currentToken);

            // Token value is set here. Other token fields have been populated in the parent abstract listener class
            currentToken.setValue(tokenString.trim());
            // Save token to the internal list of tokens participating in the hashing
            accumulatedTokens.add(currentToken.clone());

            // Calculate new hash code and ask Registry for any matches
            int calculatedHashCode = Objects.hash(accumulatedTokens);
            logger.debug("New Hash Code calculated : {}", calculatedHashCode);
            logger.debug("Tokens involved in hash code calculation till now: \n{}", accumulatedTokens);

            Optional<PublisherSpecification> optSpec = registry.findSpecByHashCode(calculatedHashCode);
            if (optSpec.isPresent()) {
                // Set the retrieved publisher specification as the active one in Registry and signal end of processing
                registry.setActiveSpec(optSpec.get());
                taskCompleted = true;
            }
        }
    }

    @Override
    public void reset() {

        // Resetting the token emitter state with a call to super
        super.reset();

        logger.info("Resetting {}", this.getClass().getName());
        taskCompleted = false;
        accumulatedTokens.clear();
        registry.setActiveSpec(null);
    }
}