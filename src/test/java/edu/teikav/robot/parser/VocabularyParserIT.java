package edu.teikav.robot.parser;

import edu.teikav.robot.parser.services.InventoryService;
import edu.teikav.robot.parser.services.PublisherSpecificationRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static edu.teikav.robot.parser.ParserStaticConstants.TEST_INPUT_RTF_DOCS_PATH;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Tag("integration")
@DisplayName("Application-Test: End-2-End Vocabulary Parser")
class VocabularyParserIT {

    private Logger logger = LoggerFactory.getLogger(VocabularyParserIT.class);

    @Autowired
    private VocabularyParser vocabularyParser;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    PublisherSpecificationRegistry registry;

    @Test
    void parseVocabularyForTwoPublishers() throws IOException, XMLStreamException {
        prepareRegistry();

        logger.info("parseVocabularyForFirstPublisher called ******************************");
        vocabularyParser.parseVocabulary(
                new File(TEST_INPUT_RTF_DOCS_PATH + "sample-vocabulary-01.rtf"));
        assertThat(inventoryService.inventorySize()).isEqualTo(4);
        logger.info("parseVocabularyForFirstPublisher finished ******************************");

        logger.info("parseVocabularyForSecondPublisher called ******************************");
        vocabularyParser.parseVocabulary(
                new File(TEST_INPUT_RTF_DOCS_PATH + "sample-vocabulary-02.rtf"));
        assertThat(inventoryService.inventorySize()).isEqualTo(4 + 5);
        logger.info("parseVocabularyForSecondPublisher finished ******************************");
    }

    private void prepareRegistry() {
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("publishers/two-publishers.yaml");
        registry.removeAllPublisherSpecs();
        registry.registerPublisherSpecifications(inputStream);
        assertThat(registry.registrySize()).isEqualTo(2);
    }

}
