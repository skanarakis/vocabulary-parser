package edu.teikav.robot.parser;

import com.rtfparserkit.parser.IRtfParser;
import com.rtfparserkit.parser.IRtfSource;
import com.rtfparserkit.parser.RtfStreamSource;
import com.rtfparserkit.parser.standard.StandardRtfParser;
import edu.teikav.robot.parser.domain.RtfCallbackHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class VocabularyParser {

    private Logger logger = LoggerFactory.getLogger(VocabularyParser.class);

    private RtfCallbackHandler publisherIdentifier;
    private RtfCallbackHandler vocabularyRecognizer;

    @Autowired
    public VocabularyParser(@Qualifier("PublisherIdentifier") RtfCallbackHandler publisherIdentifier,
                            @Qualifier("VocabularyRecognizer") RtfCallbackHandler vocabularyRecognizer) {
        this.publisherIdentifier = publisherIdentifier;
        this.vocabularyRecognizer = vocabularyRecognizer;
    }

    public void parseVocabulary(final File vocabularyRtfDoc) throws IOException {

        if (vocabularyRtfDoc == null) {
            throw new NullPointerException("Input RTF vocabulary document is null");
        }

        String docAbsolutePath = vocabularyRtfDoc.getAbsolutePath();
        IRtfSource rtfSource = newRtfSource(docAbsolutePath);

        // Utilizing 3rd party library for RTF
        IRtfParser parser = new StandardRtfParser();

        // Clear prior state
        clearInternalState();

        // Make the first pass
        logger.info("Parser will attempt the first pass - Attempt to identify publisher");
        parser.parse(rtfSource, publisherIdentifier);

        // Need to reset input stream
        rtfSource = newRtfSource(docAbsolutePath);

        // Make the second pass
        logger.info("Parser will attempt the second pass - Attempt to recognize vocabulary");
        parser.parse(rtfSource, vocabularyRecognizer);
    }

    private IRtfSource newRtfSource(String absPath) throws FileNotFoundException {
        InputStream rtfStream = new FileInputStream(absPath);
        return new RtfStreamSource(rtfStream);
    }

    private void clearInternalState() {
        publisherIdentifier.reset();
        vocabularyRecognizer.reset();
    }
}
