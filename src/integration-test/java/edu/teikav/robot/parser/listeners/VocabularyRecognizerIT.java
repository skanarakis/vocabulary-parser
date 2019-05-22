package edu.teikav.robot.parser.listeners;

import com.rtfparserkit.parser.IRtfListener;
import com.rtfparserkit.parser.IRtfParser;
import com.rtfparserkit.parser.IRtfSource;
import com.rtfparserkit.parser.RtfStreamSource;
import com.rtfparserkit.parser.standard.StandardRtfParser;
import edu.teikav.robot.parser.FileUtils;
import edu.teikav.robot.parser.IntegrationTest;
import edu.teikav.robot.parser.domain.PublisherDocumentInput;
import edu.teikav.robot.parser.domain.SpeechPart;
import edu.teikav.robot.parser.services.InMemoryInventoryServiceImpl;
import edu.teikav.robot.parser.services.InventoryService;
import edu.teikav.robot.parser.services.PublisherSpecificationRegistry;
import edu.teikav.robot.parser.services.YAMLPublisherSpecRegistryImpl;
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

import javax.xml.stream.XMLStreamException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static edu.teikav.robot.parser.ParserStaticConstants.*;

@RunWith(SpringRunner.class)
@Category(IntegrationTest.class)
public class VocabularyRecognizerIT {

    private Logger logger = LoggerFactory.getLogger(VocabularyRecognizerIT.class);

    private static PublisherSpecificationRegistry registry;

    private InventoryService inventoryService;

    @Autowired
    @Qualifier("FirstPassOutputStream")
    private OutputStream firstPassOutputStream;

    @Autowired
    @Qualifier("SecondPassOutputStream")
    private OutputStream secondPassOutputStream;

    @BeforeClass
    public static void preloadRegistry() {

        registry = new YAMLPublisherSpecRegistryImpl(new Yaml(new Constructor(PublisherDocumentInput.class)));
        InputStream publishersInputStream = VocabularyRecognizerIT.class
                .getClassLoader()
                .getResourceAsStream("publishers/all-publishers.yaml");
        registry.registerPublisherSpecifications(publishersInputStream);
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
        IRtfListener vocabularyIdentifier = new PublisherIdentifier(registry, firstPassOutputStream);
        parser.parse(source, vocabularyIdentifier);

        // Need to reset input stream
        inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH +
                "sample-vocabulary-01.rtf");
        source = new RtfStreamSource(inputStream);

        // Make the second pass
        IRtfListener vocabularySemanticsTokenizer = new VocabularyRecognizer(registry,
                inventoryService, secondPassOutputStream);
        parser.parse(source, vocabularySemanticsTokenizer);

        Assertions.assertThat(inventoryService.inventorySize()).isEqualTo(4);
        Assertions.assertThat(inventoryService.isInventoried("frog")).isTrue();
        Assertions.assertThat(inventoryService.getItem("frog").getTermType())
                .isEqualTo(SpeechPart.NOUN);
        Assertions.assertThat(inventoryService.getItem("frog").getExample())
                .isEqualTo("Frogs are small green animals.");
        Assertions.assertThat(inventoryService.getItem("frog").getTranslation())
                .isEqualTo("βάτραχος");

    }

    @Test
    public void inventoriesVocabularyForSecondPublisher() throws IOException, XMLStreamException {
        InputStream inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH +
                "sample-vocabulary-02.rtf");
        IRtfSource source = new RtfStreamSource(inputStream);
        IRtfParser parser = new StandardRtfParser();

        // Make the first pass
        IRtfListener vocabularyIdentifier = new PublisherIdentifier(registry, firstPassOutputStream);
        parser.parse(source, vocabularyIdentifier);

        // Need to reset input stream
        inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH +
                "sample-vocabulary-02.rtf");
        source = new RtfStreamSource(inputStream);

        // Make the second pass
        IRtfListener vocabularySemanticsTokenizer = new VocabularyRecognizer(registry,
                inventoryService, secondPassOutputStream);
        parser.parse(source, vocabularySemanticsTokenizer);

        Assertions.assertThat(inventoryService.inventorySize()).isEqualTo(5);
        Assertions.assertThat(inventoryService.isInventoried("incandescent")).isTrue();
        Assertions.assertThat(inventoryService.isInventoried("fluorescent")).isTrue();
        Assertions.assertThat(inventoryService.isInventoried("prolonged")).isTrue();
        Assertions.assertThat(inventoryService.isInventoried("shade")).isTrue();
        Assertions.assertThat(inventoryService.isInventoried("subtle")).isTrue();
    }

    @Test
    public void inventoriesVocabularyForThirdPublisher() throws IOException, XMLStreamException {
        InputStream inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH +
                "OurW-2b.rtf");
        IRtfSource source = new RtfStreamSource(inputStream);
        IRtfParser parser = new StandardRtfParser();

        // Make the first pass
        IRtfListener vocabularyIdentifier = new PublisherIdentifier(registry, firstPassOutputStream);
        parser.parse(source, vocabularyIdentifier);

        // Need to reset input stream
        inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH +
                "OurW-2b.rtf");
        source = new RtfStreamSource(inputStream);

        // Make the second pass
        IRtfListener vocabularySemanticsTokenizer = new VocabularyRecognizer(registry,
                inventoryService, secondPassOutputStream);
        parser.parse(source, vocabularySemanticsTokenizer);

        Assertions.assertThat(inventoryService.inventorySize()).isEqualTo(7);
        Assertions.assertThat(inventoryService.isInventoried("neighbour")).isTrue();
        Assertions.assertThat(inventoryService.isInventoried("lighthouse")).isTrue();
        Assertions.assertThat(inventoryService.isInventoried("keeper")).isTrue();
        Assertions.assertThat(inventoryService.isInventoried("coast")).isTrue();
        Assertions.assertThat(inventoryService.isInventoried("mean")).isTrue();
        Assertions.assertThat(inventoryService.isInventoried("tower")).isTrue();
        Assertions.assertThat(inventoryService.isInventoried("shine")).isTrue();
    }

    @TestConfiguration
    static class VocabularyRecognizerTestConfiguration {

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

