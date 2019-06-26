package edu.teikav.robot.parser.controllers;

import edu.teikav.robot.parser.VocabularyParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

@RestController
@RequestMapping("/voc-parser")
public class VocParserRestController {

    private Logger logger = LoggerFactory.getLogger(VocParserRestController.class);

    private VocabularyParser vocabularyParser;

    @Autowired
    public VocParserRestController(VocabularyParser vocabularyParser) {
        this.vocabularyParser = vocabularyParser;
    }

    @PostMapping(value = "/documents", consumes = "text/plain;charset=UTF-8")
    public void sendRtfForProcessing(@RequestBody String rtfDoc,
                                     @RequestParam(value="publisher", required=false) String publisher) {

        logger.info("Sending RTF for processing");
        try {
            if (StringUtils.isEmpty(publisher)) {
                vocabularyParser.parseVocabulary(rtfDoc);
            } else {
                vocabularyParser.parseVocabulary(rtfDoc, publisher);
            }
        } catch (IOException e) {
            logger.error("IO Exception :\n{}", e.getMessage());
        } catch (XMLStreamException e) {
            logger.error("XML Stream Exception :\n{}", e.getMessage());
        }
        logger.info("RTF successfully consumed");
    }
}
