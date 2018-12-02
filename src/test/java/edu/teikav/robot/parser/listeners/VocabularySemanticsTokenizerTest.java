package edu.teikav.robot.parser.listeners;

import com.rtfparserkit.parser.IRtfListener;
import com.rtfparserkit.parser.IRtfParser;
import com.rtfparserkit.parser.IRtfSource;
import com.rtfparserkit.parser.RtfStreamSource;
import com.rtfparserkit.parser.standard.StandardRtfParser;
import edu.teikav.robot.parser.domain.PublisherGrammar;
import edu.teikav.robot.parser.domain.TermGrammarTypes;
import edu.teikav.robot.parser.services.InMemoryInventoryServiceImpl;
import edu.teikav.robot.parser.services.InventoryService;
import edu.teikav.robot.parser.services.PublisherGrammarRegistry;
import edu.teikav.robot.parser.services.YAMLBasedPublisherGrammarRegistryImpl;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
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

import javax.xml.stream.XMLStreamException;
import java.io.*;

import static edu.teikav.robot.parser.ParserStaticConstants.*;

@RunWith(SpringRunner.class)
public class VocabularySemanticsTokenizerTest {

    private Logger logger = LoggerFactory.getLogger(VocabularySemanticsTokenizerTest.class);

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
        InputStream publishersInputStream = VocabularySemanticsTokenizerTest.class
                .getClassLoader()
                .getResourceAsStream("publishers/two-publishers.yaml");
        registry.loadMultipleGrammars(publishersInputStream);
        Assertions.assertThat(registry.numberOfGrammars()).isEqualTo(2);
    }

    @Before
    public void initialize() {
        inventoryService = new InMemoryInventoryServiceImpl();
    }

    @Test
    public void shouldParseVocabularyRTFAsXML_forFirstPublisher() throws IOException, XMLStreamException {
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
    public void shouldParseVocabularyRTFAsXML_forSecondPublisher() throws IOException, XMLStreamException {
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

    @TestConfiguration
    static class VocabularyIdentifierTestConfiguration {

        @Bean
        @Qualifier("FirstPassOutputStream")
        OutputStream firstPassOutputStream() throws FileNotFoundException {
            return new FileOutputStream(TEST_OUTPUT_XML_DOCS_PATH +
                    FIRST_PASS_TEST_OUTPUT_XML_FILENAME);
        }

        @Bean
        @Qualifier("SecondPassOutputStream")
        OutputStream secondPassOutputStream() throws FileNotFoundException {
            return new FileOutputStream(TEST_OUTPUT_XML_DOCS_PATH +
                    SECOND_PASS_TEST_OUTPUT_XML_FILENAME);
        }
    }
}

