package edu.teikav.robot.parser.controllers;

import edu.teikav.robot.parser.services.VocabularyParsingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/voc-parser")
public class VocParserRestController {

    private Logger logger = LoggerFactory.getLogger(VocParserRestController.class);

    private VocabularyParsingService vocabularyParsingService;

    @Autowired
    public VocParserRestController(VocabularyParsingService vocabularyParsingService) {
        this.vocabularyParsingService = vocabularyParsingService;
    }

    @PostMapping(value = "/documents", consumes = "text/plain;charset=UTF-8")
    public void sendRtfForProcessing(@RequestBody String rtfDoc,
                                     @RequestParam(value="publisher", required=false) String publisher) {

        logger.info("Sending RTF for processing");
        if (StringUtils.isEmpty(publisher)) {
            vocabularyParsingService.parseVocabulary(rtfDoc);
        } else {
            vocabularyParsingService.parseVocabulary(rtfDoc, publisher);
        }
        logger.info("RTF successfully consumed");
    }
}
