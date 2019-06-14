package edu.teikav.robot.parser;

import com.rtfparserkit.parser.IRtfParser;
import com.rtfparserkit.parser.IRtfSource;
import com.rtfparserkit.parser.RtfStreamSource;
import com.rtfparserkit.parser.standard.StandardRtfParser;
import edu.teikav.robot.parser.processors.PublisherIdentifier;
import edu.teikav.robot.parser.processors.VocabularyRecognizer;
import edu.teikav.robot.parser.processors.VocabularySeparator;
import edu.teikav.robot.parser.services.InventoryService;
import edu.teikav.robot.parser.services.PublisherSpecificationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.Objects;

@Component
public class VocabularyParser {

    private Logger logger = LoggerFactory.getLogger(VocabularyParser.class);

    private final PublisherSpecificationRegistry registry;
    private final InventoryService inventoryService;

    @Autowired
    public VocabularyParser(PublisherSpecificationRegistry registry, InventoryService inventoryService) {
        this.registry = registry;
        this.inventoryService = inventoryService;
    }

    public void parseVocabulary(final String rtfDoc) throws IOException, XMLStreamException {

        Objects.requireNonNull(rtfDoc,"Empty contents passed for parsing");
        // Utilizing 3rd party library for RTF
        IRtfSource rtfSource = newRtfSourceFromString(rtfDoc);

        doParse(rtfSource);
    }

    public void parseVocabulary(final File vocabularyRtfDoc) throws IOException, XMLStreamException {

        Objects.requireNonNull(vocabularyRtfDoc,"Input RTF vocabulary document is null");
        String docAbsolutePath = vocabularyRtfDoc.getAbsolutePath();

        // Utilizing 3rd party library for RTF
        IRtfSource rtfSource = newRtfSourceFromFile(docAbsolutePath);

        doParse(rtfSource);
    }

    private void doParse(IRtfSource rtfSource) throws XMLStreamException, IOException {
        IRtfParser parser = new StandardRtfParser();

        VocabularySeparator vocabularySeparator = new VocabularySeparator();
        parser.parse(rtfSource, vocabularySeparator);

        // Make the first pass to identify publisher
        logger.info("Parser will attempt the first pass - Attempt to identify publisher");
        PublisherIdentifier publisherIdentifier = new PublisherIdentifier(registry, vocabularySeparator);
        publisherIdentifier.identifyPublisher();

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
