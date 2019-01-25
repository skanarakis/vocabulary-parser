package edu.teikav.robot.parser.listeners;

import static edu.teikav.robot.parser.FileUtils.getOutputStream;
import static edu.teikav.robot.parser.ParserStaticConstants.TEST_INPUT_RTF_DOCS_PATH;
import static edu.teikav.robot.parser.ParserStaticConstants.TEST_OUTPUT_XML_DOCS_PATH;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.stream.XMLStreamException;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.rtfparserkit.parser.IRtfListener;
import com.rtfparserkit.parser.IRtfParser;
import com.rtfparserkit.parser.IRtfSource;
import com.rtfparserkit.parser.RtfStreamSource;
import com.rtfparserkit.parser.standard.StandardRtfParser;

import edu.teikav.robot.parser.IntegrationTest;

@Category(IntegrationTest.class)
public class TokenProcessorIT {

    @Test
    public void processTokenForOurW2bPublisherFormat() throws IOException, XMLStreamException {
        InputStream inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH + "OurW-2b-complete.rtf");
        IRtfSource source = new RtfStreamSource(inputStream);
        IRtfParser parser = new StandardRtfParser();

        OutputStream outputStream = getOutputStream(TEST_OUTPUT_XML_DOCS_PATH + "test-token-processing-ourW2b.xml");
        IRtfListener testProcessor = new AbstractRTFCommandsCallbackProcessor(outputStream) {
            @Override
            protected void processToken(String token) {
                // Do nothing
            }
        };
        parser.parse(source, testProcessor);
    }

    @Test
    public void processTokenForLikeEnPublisherFormat() throws IOException, XMLStreamException {
        InputStream inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH + "LikeEn.rtf");
        IRtfSource source = new RtfStreamSource(inputStream);
        IRtfParser parser = new StandardRtfParser();
        OutputStream outputStream = getOutputStream(TEST_OUTPUT_XML_DOCS_PATH + "test-token-processing-likeEn.xml");
        IRtfListener testProcessor = new AbstractRTFCommandsCallbackProcessor(outputStream) {
            @Override
            protected void processToken(String token) {
                // Do nothing
            }
        };
        parser.parse(source, testProcessor);
    }
}