package edu.teikav.robot.parser.listeners;

import static edu.teikav.robot.parser.ParserStaticConstants.FIRST_PASS_TEST_OUTPUT_XML_FILENAME;
import static edu.teikav.robot.parser.ParserStaticConstants.SECOND_PASS_TEST_OUTPUT_XML_FILENAME;
import static edu.teikav.robot.parser.ParserStaticConstants.TEST_INPUT_RTF_DOCS_PATH;
import static edu.teikav.robot.parser.ParserStaticConstants.TEST_OUTPUT_XML_DOCS_PATH;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.stream.XMLStreamException;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.rtfparserkit.parser.IRtfListener;
import com.rtfparserkit.parser.IRtfParser;
import com.rtfparserkit.parser.IRtfSource;
import com.rtfparserkit.parser.RtfStreamSource;
import com.rtfparserkit.parser.standard.StandardRtfParser;

import edu.teikav.robot.parser.FileUtils;
import edu.teikav.robot.parser.IntegrationTest;
import edu.teikav.robot.parser.domain.PublisherGrammar;
import edu.teikav.robot.parser.domain.TermGrammarTypes;
import edu.teikav.robot.parser.services.InMemoryInventoryServiceImpl;
import edu.teikav.robot.parser.services.InventoryService;
import edu.teikav.robot.parser.services.PublisherGrammarRegistry;
import edu.teikav.robot.parser.services.YAMLBasedPublisherGrammarRegistryImpl;

@RunWith(SpringRunner.class)
@Category(IntegrationTest.class)
public class VocabularySemanticsTokenizerIT {

    private Logger logger = LoggerFactory.getLogger(VocabularySemanticsTokenizerIT.class);

    private static PublisherGrammarRegistry registry;

    private InventoryService inventoryService;

    @Autowired
    @Qualifier("FirstPassOutputStream")
    private OutputStream firstPassOutputStream;

    @Autowired
    @Qualifier("SecondPassOutputStream")
    private OutputStream secondPassOutputStream;

    @BeforeClass
    public static void preloadRegistry() {

        registry = new YAMLBasedPublisherGrammarRegistryImpl(new Yaml(new Constructor(PublisherGrammar.class)));
        InputStream publishersInputStream = VocabularySemanticsTokenizerIT.class
                .getClassLoader()
                .getResourceAsStream("publishers/all-publishers.yaml");
        registry.loadMultipleGrammars(publishersInputStream);
    }

    @Before
    public void initialize() {
        inventoryService = new InMemoryInventoryServiceImpl();
    }

    @Test
    public void inventoriesVocabularyForFirstPublisher() throws IOException, XMLStreamException {
        InputStream inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH +
                "sample-vocabulary-01.rtf");
        IRtfSource source = new RtfStreamSource(inputStream);
        IRtfParser parser = new StandardRtfParser();

        // Make the first pass
        IRtfListener vocabularyIdentifier = new VocabularyIdentifier(registry, firstPassOutputStream);
        parser.parse(source, vocabularyIdentifier);

        // Need to reset input stream
        inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH +
                "sample-vocabulary-01.rtf");
        source = new RtfStreamSource(inputStream);

        // Make the second pass
        IRtfListener vocabularySemanticsTokenizer = new VocabularySemanticsTokenizer(registry,
                inventoryService, secondPassOutputStream);
        parser.parse(source, vocabularySemanticsTokenizer);

        Assertions.assertThat(inventoryService.numberOfInventoryTerms()).isEqualTo(4);
        Assertions.assertThat(inventoryService.existsInventoryItem("frog")).isTrue();
        Assertions.assertThat(inventoryService.getInventoryItem("frog").getTermType())
                .isEqualTo(TermGrammarTypes.NOUN);
        Assertions.assertThat(inventoryService.getInventoryItem("frog").getExample())
                .isEqualTo("Frogs are small green animals.");
        Assertions.assertThat(inventoryService.getInventoryItem("frog").getTranslation())
                .isEqualTo("βάτραχος");

    }

    @Test
    public void inventoriesVocabularyForSecondPublisher() throws IOException, XMLStreamException {
        InputStream inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH +
                "sample-vocabulary-02.rtf");
        IRtfSource source = new RtfStreamSource(inputStream);
        IRtfParser parser = new StandardRtfParser();

        // Make the first pass
        IRtfListener vocabularyIdentifier = new VocabularyIdentifier(registry, firstPassOutputStream);
        parser.parse(source, vocabularyIdentifier);

        // Need to reset input stream
        inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH +
                "sample-vocabulary-02.rtf");
        source = new RtfStreamSource(inputStream);

        // Make the second pass
        IRtfListener vocabularySemanticsTokenizer = new VocabularySemanticsTokenizer(registry,
                inventoryService, secondPassOutputStream);
        parser.parse(source, vocabularySemanticsTokenizer);

        Assertions.assertThat(inventoryService.numberOfInventoryTerms()).isEqualTo(5);
        Assertions.assertThat(inventoryService.existsInventoryItem("incandescent")).isTrue();
        Assertions.assertThat(inventoryService.existsInventoryItem("fluorescent")).isTrue();
        Assertions.assertThat(inventoryService.existsInventoryItem("prolonged")).isTrue();
        Assertions.assertThat(inventoryService.existsInventoryItem("shade")).isTrue();
        Assertions.assertThat(inventoryService.existsInventoryItem("subtle")).isTrue();
    }

    @Test
    public void inventoriesVocabularyForThirdPublisher() throws IOException, XMLStreamException {
        InputStream inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH +
                "OurW-2b.rtf");
        IRtfSource source = new RtfStreamSource(inputStream);
        IRtfParser parser = new StandardRtfParser();

        // Make the first pass
        IRtfListener vocabularyIdentifier = new VocabularyIdentifier(registry, firstPassOutputStream);
        parser.parse(source, vocabularyIdentifier);

        // Need to reset input stream
        inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH +
                "OurW-2b.rtf");
        source = new RtfStreamSource(inputStream);

        // Make the second pass
        IRtfListener vocabularySemanticsTokenizer = new VocabularySemanticsTokenizer(registry,
                inventoryService, secondPassOutputStream);
        parser.parse(source, vocabularySemanticsTokenizer);

        Assertions.assertThat(inventoryService.numberOfInventoryTerms()).isEqualTo(7);
        Assertions.assertThat(inventoryService.existsInventoryItem("neighbour")).isTrue();
        Assertions.assertThat(inventoryService.existsInventoryItem("lighthouse")).isTrue();
        Assertions.assertThat(inventoryService.existsInventoryItem("keeper")).isTrue();
        Assertions.assertThat(inventoryService.existsInventoryItem("coast")).isTrue();
        Assertions.assertThat(inventoryService.existsInventoryItem("mean")).isTrue();
        Assertions.assertThat(inventoryService.existsInventoryItem("tower")).isTrue();
        Assertions.assertThat(inventoryService.existsInventoryItem("shine")).isTrue();
    }

    @TestConfiguration
    static class VocabularyIdentifierTestConfiguration {

        @Bean
        @Qualifier("FirstPassOutputStream")
        OutputStream firstPassOutputStream() throws IOException {
            return FileUtils.getOutputStream(TEST_OUTPUT_XML_DOCS_PATH +
                    FIRST_PASS_TEST_OUTPUT_XML_FILENAME);
        }

        @Bean
        @Qualifier("SecondPassOutputStream")
        OutputStream secondPassOutputStream() throws IOException {
            return FileUtils.getOutputStream(TEST_OUTPUT_XML_DOCS_PATH +
                    SECOND_PASS_TEST_OUTPUT_XML_FILENAME);
        }
    }
}

