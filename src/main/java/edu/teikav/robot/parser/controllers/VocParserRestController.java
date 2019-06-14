package edu.teikav.robot.parser.controllers;

import edu.teikav.robot.parser.VocabularyParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public void sendRtfForProcessing(@RequestBody String rtfDoc) {

        logger.info("Sending RTF for processing");
        try {
            vocabularyParser.parseVocabulary(rtfDoc);
        } catch (IOException e) {
            logger.error("IO Exception :\n{}", e.getMessage());
        } catch (XMLStreamException e) {
            logger.error("XML Stream Exception :\n{}", e.getMessage());
        }
        logger.info("RTF successfully consumed");
    }
}
