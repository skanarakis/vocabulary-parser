package edu.teikav.robot.parser.integration;

import com.rtfparserkit.parser.IRtfListener;
import com.rtfparserkit.parser.IRtfParser;
import com.rtfparserkit.parser.IRtfSource;
import com.rtfparserkit.parser.RtfStreamSource;
import com.rtfparserkit.parser.standard.StandardRtfParser;
import edu.teikav.robot.parser.processors.VocabularySeparator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLStreamException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static edu.teikav.robot.parser.ParserStaticConstants.TEST_INPUT_RTF_DOCS_PATH;
import static edu.teikav.robot.parser.ParserStaticConstants.TEST_OUTPUT_XML_DOCS_PATH;
import static edu.teikav.robot.parser.util.FileUtils.getOutputStream;

@Tag("integration")
@DisplayName("Integration-Test: Vocabulary Separator Module")
class VocabularySeparatorIT {

    @Test
    void processTokenForOurW2bPublisherFormat() throws IOException, XMLStreamException {
        InputStream inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH + "OurW-2b-complete.rtf");
        IRtfSource source = new RtfStreamSource(inputStream);
        IRtfParser parser = new StandardRtfParser();

        OutputStream outputStream = getOutputStream(TEST_OUTPUT_XML_DOCS_PATH + "test-token-processing-ourW2b.xml");
        IRtfListener vocabularySeparator = new VocabularySeparator(outputStream);
        parser.parse(source, vocabularySeparator);
    }

    @Test
    void processTokenForLikeEnPublisherFormat() throws IOException, XMLStreamException {
        InputStream inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH + "LikeEn.rtf");
        IRtfSource source = new RtfStreamSource(inputStream);
        IRtfParser parser = new StandardRtfParser();
        OutputStream outputStream = getOutputStream(TEST_OUTPUT_XML_DOCS_PATH + "test-token-processing-likeEn.xml");
        IRtfListener vocabularySeparator = new VocabularySeparator(outputStream);
        parser.parse(source, vocabularySeparator);
    }

    @Test
    void processTokenForGePublisherFormat() throws IOException, XMLStreamException {
        InputStream inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH + "GE-B2-b.rtf");
        IRtfSource source = new RtfStreamSource(inputStream);
        IRtfParser parser = new StandardRtfParser();
        OutputStream outputStream = getOutputStream(TEST_OUTPUT_XML_DOCS_PATH + "test-token-processing-GE-B2-b.xml");
        IRtfListener vocabularySeparator = new VocabularySeparator(outputStream);
        parser.parse(source, vocabularySeparator);
    }

    @Test
    void processTokenForPublisherEFormat() throws IOException, XMLStreamException {
        InputStream inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH + "PubE.rtf");
        IRtfSource source = new RtfStreamSource(inputStream);
        IRtfParser parser = new StandardRtfParser();
        OutputStream outputStream = getOutputStream(TEST_OUTPUT_XML_DOCS_PATH + "test-token-processing-Pub-E.xml");
        IRtfListener vocabularySeparator = new VocabularySeparator(outputStream);
        parser.parse(source, vocabularySeparator);
    }
}