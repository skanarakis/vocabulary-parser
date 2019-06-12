package edu.teikav.robot.parser.processors;

import com.rtfparserkit.parser.IRtfListener;
import com.rtfparserkit.parser.IRtfParser;
import com.rtfparserkit.parser.IRtfSource;
import com.rtfparserkit.parser.RtfStreamSource;
import com.rtfparserkit.parser.standard.StandardRtfParser;
import edu.teikav.robot.parser.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.stream.XMLStreamException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static edu.teikav.robot.parser.FileUtils.getOutputStream;
import static edu.teikav.robot.parser.ParserStaticConstants.TEST_INPUT_RTF_DOCS_PATH;
import static edu.teikav.robot.parser.ParserStaticConstants.TEST_OUTPUT_XML_DOCS_PATH;

@Category(IntegrationTest.class)
public class VocabularySeparatorIT {

    @Test
    public void processTokenForOurW2bPublisherFormat() throws IOException, XMLStreamException {
        InputStream inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH + "OurW-2b-complete.rtf");
        IRtfSource source = new RtfStreamSource(inputStream);
        IRtfParser parser = new StandardRtfParser();

        OutputStream outputStream = getOutputStream(TEST_OUTPUT_XML_DOCS_PATH + "test-token-processing-ourW2b.xml");
        IRtfListener vocabularySeparator = new VocabularySeparator(outputStream);
        parser.parse(source, vocabularySeparator);
    }

    @Test
    public void processTokenForLikeEnPublisherFormat() throws IOException, XMLStreamException {
        InputStream inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH + "LikeEn.rtf");
        IRtfSource source = new RtfStreamSource(inputStream);
        IRtfParser parser = new StandardRtfParser();
        OutputStream outputStream = getOutputStream(TEST_OUTPUT_XML_DOCS_PATH + "test-token-processing-likeEn.xml");
        IRtfListener vocabularySeparator = new VocabularySeparator(outputStream);
        parser.parse(source, vocabularySeparator);
    }
}