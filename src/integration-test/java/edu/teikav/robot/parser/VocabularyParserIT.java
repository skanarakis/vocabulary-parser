package edu.teikav.robot.parser;

import edu.teikav.robot.parser.services.InventoryService;
import edu.teikav.robot.parser.services.PublisherSpecificationRegistry;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static edu.teikav.robot.parser.ParserStaticConstants.TEST_INPUT_RTF_DOCS_PATH;

@RunWith(SpringRunner.class)
@SpringBootTest
@Category(IntegrationTest.class)
public class VocabularyParserIT {

    private Logger logger = LoggerFactory.getLogger(VocabularyParserIT.class);

    @Autowired
    private VocabularyParser vocabularyParser;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    PublisherSpecificationRegistry registry;

    @Test
    public void parseVocabularyForTwoPublishers() throws IOException, XMLStreamException {
        prepareRegistry();

        logger.info("parseVocabularyForFirstPublisher called ******************************");
        vocabularyParser.parseVocabulary(
                new File(TEST_INPUT_RTF_DOCS_PATH + "sample-vocabulary-01.rtf"));
        Assertions.assertThat(inventoryService.inventorySize()).isEqualTo(4);
        logger.info("parseVocabularyForFirstPublisher finished ******************************");

        logger.info("parseVocabularyForSecondPublisher called ******************************");
        vocabularyParser.parseVocabulary(
                new File(TEST_INPUT_RTF_DOCS_PATH + "sample-vocabulary-02.rtf"));
        Assertions.assertThat(inventoryService.inventorySize()).isEqualTo(4 + 5);
        logger.info("parseVocabularyForSecondPublisher finished ******************************");
    }

    private void prepareRegistry() throws IOException {
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("publishers/two-publishers.yaml");
        registry.removeAllPublisherSpecs();
        registry.registerPublisherSpecifications(inputStream);
        Assertions.assertThat(registry.registrySize()).isEqualTo(2);
    }

}
