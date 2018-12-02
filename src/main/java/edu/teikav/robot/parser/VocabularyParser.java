package edu.teikav.robot.parser;

import com.rtfparserkit.parser.IRtfParser;
import com.rtfparserkit.parser.IRtfSource;
import com.rtfparserkit.parser.RtfStreamSource;
import com.rtfparserkit.parser.standard.StandardRtfParser;
import edu.teikav.robot.parser.domain.RTFParserCallbackProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class VocabularyParser {

    private Logger logger = LoggerFactory.getLogger(VocabularyParser.class);

    private RTFParserCallbackProcessor firstPassParser;
    private RTFParserCallbackProcessor secondPassParser;

    public VocabularyParser(@Qualifier("FirstPassParser") RTFParserCallbackProcessor firstPassParser,
                            @Qualifier("SecondPassParser") RTFParserCallbackProcessor secondPassParser) {
        this.firstPassParser = firstPassParser;
        this.secondPassParser = secondPassParser;
    }

    public void parseVocabulary(File inputDocumentFile) throws IOException {
        InputStream inputStream = new FileInputStream(inputDocumentFile.getAbsolutePath());

        IRtfSource source = new RtfStreamSource(inputStream);
        IRtfParser parser = new StandardRtfParser();

        // Make the first pass
        logger.info("Parser will attempt the first pass");
        parser.parse(source, firstPassParser);

        // Need to reset input stream
        inputStream = new FileInputStream(inputDocumentFile.getAbsolutePath());
        source = new RtfStreamSource(inputStream);

        // Make the second pass
        logger.info("Parser will attempt the second pass");
        parser.parse(source, secondPassParser);
    }

    public void reset() {
        firstPassParser.reset();
        secondPassParser.reset();
    }
}
