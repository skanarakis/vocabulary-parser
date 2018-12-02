package edu.teikav.robot.parser;

import edu.teikav.robot.parser.services.InventoryService;
import edu.teikav.robot.parser.services.PublisherGrammarRegistry;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static edu.teikav.robot.parser.ParserStaticConstants.TEST_INPUT_RTF_DOCS_PATH;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VocabularyParserTest {

    private Logger logger = LoggerFactory.getLogger(VocabularyParserTest.class);

    @Autowired
    private VocabularyParser vocabularyParser;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    PublisherGrammarRegistry registry;

    @After
    public void cleanState() {
        logger.info("resetParser called ******************************");
        vocabularyParser.reset();
        inventoryService.empty();
    }

    @Test
    public void parseVocabularyForFirstPublisher() throws IOException {
        logger.info("parseVocabularyForFirstPublisher called ******************************");
        prepareRegistry();
        vocabularyParser.parseVocabulary(
                new File(TEST_INPUT_RTF_DOCS_PATH + "sample-vocabulary-01.rtf"));
        Assertions.assertThat(inventoryService.numberOfInventoryTerms()).isEqualTo(4);
        logger.info("parseVocabularyForFirstPublisher finished ******************************");
    }

    @Test
    public void parseVocabularyForSecondPublisher() throws IOException {
        logger.info("parseVocabularyForSecondPublisher called ******************************");
        prepareRegistry();
        vocabularyParser.parseVocabulary(
                new File(TEST_INPUT_RTF_DOCS_PATH + "sample-vocabulary-02.rtf"));
        Assertions.assertThat(inventoryService.numberOfInventoryTerms()).isEqualTo(5);
        logger.info("parseVocabularyForSecondPublisher finished ******************************");
    }

    private void prepareRegistry() throws IOException {
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("publishers/two-publishers.yaml");
        registry.clean();
        registry.loadMultipleGrammars(inputStream);
        Assertions.assertThat(registry.numberOfGrammars()).isEqualTo(2);


    }

}
