package edu.teikav.robot.parser.services;

import com.rtfparserkit.parser.IRtfParser;
import com.rtfparserkit.parser.IRtfSource;
import com.rtfparserkit.parser.RtfStreamSource;
import com.rtfparserkit.parser.standard.StandardRtfParser;
import edu.teikav.robot.parser.exceptions.VocabularyParsingException;
import edu.teikav.robot.parser.processors.PublisherIdentifier;
import edu.teikav.robot.parser.processors.VocabularyRecognizer;
import edu.teikav.robot.parser.processors.VocabularySeparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.Objects;

@Service
public class RtfVocabularyParserImpl implements VocabularyParsingService {

    private Logger logger = LoggerFactory.getLogger(RtfVocabularyParserImpl.class);

    private final PublisherSpecificationRegistry registry;
    private final InventoryService inventoryService;

    @Autowired
    public RtfVocabularyParserImpl(PublisherSpecificationRegistry registry, InventoryService inventoryService) {
        this.registry = registry;
        this.inventoryService = inventoryService;
    }

    @Override
    public void parseVocabulary(final String vocabularyDocument) {

        Objects.requireNonNull(vocabularyDocument,"Empty contents passed for parsing");
        // Utilizing 3rd party library for RTF
        IRtfSource rtfSource = newRtfSourceFromString(vocabularyDocument);

        doParse(rtfSource, null);
    }

    @Override
    public void parseVocabulary(final String vocabularyDocument, String publisher) {
        Objects.requireNonNull(vocabularyDocument,"Input RTF vocabulary document is null");
        Objects.requireNonNull(publisher,"Publisher name is null");

        // Utilizing 3rd party library for RTF
        IRtfSource rtfSource = newRtfSourceFromString(vocabularyDocument);

        doParse(rtfSource, publisher);
    }

    @Override
    public void parseVocabulary(final File vocabularyRtfDoc) {

        Objects.requireNonNull(vocabularyRtfDoc,"Input RTF vocabulary document is null");
        String docAbsolutePath = vocabularyRtfDoc.getAbsolutePath();

        // Utilizing 3rd party library for RTF
        IRtfSource rtfSource;
        try {
            rtfSource = newRtfSourceFromFile(docAbsolutePath);
        } catch (FileNotFoundException e) {
            throw new VocabularyParsingException(e);
        }

        doParse(rtfSource, null);
    }

    private void doParse(IRtfSource rtfSource, String publisher) {

        VocabularySeparator vocabularySeparator = new VocabularySeparator();

        IRtfParser parser = new StandardRtfParser();
        try {
            parser.parse(rtfSource, vocabularySeparator);
        } catch (IOException e) {
            throw new VocabularyParsingException(e);
        }

        if (StringUtils.isEmpty(publisher)) {
            registry.resetActiveSpec();
            // Make the first pass to identify publisher
            logger.info("Parser will attempt the first pass - Attempt to identify publisher");
            PublisherIdentifier publisherIdentifier = new PublisherIdentifier(registry, vocabularySeparator);
            publisherIdentifier.identifyPublisher();
        } else {
            registry.setActiveSpec(publisher);
        }

        // Make the second pass to recognize vocabulary structure
        logger.info("Parser will attempt the second pass - Attempt to recognize vocabulary");
        VocabularyRecognizer vocabularyRecognizer = new VocabularyRecognizer(registry, inventoryService);
        vocabularyRecognizer.recognizeVocabulary(vocabularySeparator.streamOfVocPartsValues());
    }

    private IRtfSource newRtfSourceFromFile(String absPath) throws FileNotFoundException {
        InputStream rtfStream = new FileInputStream(absPath);
        return new RtfStreamSource(rtfStream);
    }

    private IRtfSource newRtfSourceFromString(String contents) {
        InputStream rtfStream =  new ByteArrayInputStream(contents.getBytes());
        return new RtfStreamSource(rtfStream);
    }
}
