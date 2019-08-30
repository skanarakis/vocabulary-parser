package edu.teikav.robot.parser.integration;

import com.rtfparserkit.parser.IRtfParser;
import com.rtfparserkit.parser.IRtfSource;
import com.rtfparserkit.parser.RtfStreamSource;
import com.rtfparserkit.parser.standard.StandardRtfParser;
import edu.teikav.robot.parser.domain.PublisherDocumentInput;
import edu.teikav.robot.parser.domain.SpeechPart;
import edu.teikav.robot.parser.processors.PublisherIdentifier;
import edu.teikav.robot.parser.processors.VocabularyRecognizer;
import edu.teikav.robot.parser.processors.VocabularySeparator;
import edu.teikav.robot.parser.services.InMemoryInventoryServiceImpl;
import edu.teikav.robot.parser.services.InventoryService;
import edu.teikav.robot.parser.services.PublisherSpecificationRegistry;
import edu.teikav.robot.parser.services.YAMLPublisherSpecRegistryImpl;
import edu.teikav.robot.parser.util.FileUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import javax.xml.stream.XMLStreamException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static edu.teikav.robot.parser.ParserStaticConstants.TEST_INPUT_RTF_DOCS_PATH;
import static edu.teikav.robot.parser.ParserStaticConstants.TEST_OUTPUT_XML_DOCS_PATH;

@Tag("integration")
@DisplayName("Integration-Test: Vocabulary Recognizer Module")
class VocabularyRecognizerIT {

    private static PublisherSpecificationRegistry registry;

    private InventoryService inventoryService;

    @BeforeAll
    static void preloadRegistry() {

        registry = new YAMLPublisherSpecRegistryImpl(new Yaml(new Constructor(PublisherDocumentInput.class)));
        InputStream publishersInputStream = VocabularyRecognizerIT.class
                .getClassLoader()
                .getResourceAsStream("publishers/all-publishers.yaml");
        registry.registerPublisherSpecifications(publishersInputStream);
    }

    @BeforeEach
    void initialize() {
        inventoryService = new InMemoryInventoryServiceImpl();
    }

    @Test
    void inventoriesVocabularyForFirstPublisher() throws IOException, XMLStreamException {

        OutputStream outputStream = FileUtils
                .getOutputStream(TEST_OUTPUT_XML_DOCS_PATH + "first-pub-voc-separation.xml");

        InputStream inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH +
                "sample-vocabulary-01.rtf");
        IRtfSource source = new RtfStreamSource(inputStream);
        IRtfParser parser = new StandardRtfParser();

        // Separate vocabulary parts
        VocabularySeparator vocabularySeparator = new VocabularySeparator(outputStream);
        parser.parse(source, vocabularySeparator);

        // Make the first pass to identify publisher
        PublisherIdentifier publisherIdentifier = new PublisherIdentifier(registry, vocabularySeparator);
        publisherIdentifier.identifyPublisher();

        // Make the second pass to recognize vocabulary structure
        VocabularyRecognizer vocabularyRecognizer = new VocabularyRecognizer(registry, inventoryService);
        vocabularyRecognizer.recognizeVocabulary(vocabularySeparator.streamOfVocPartsValues());

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
    void inventoriesVocabularyForSecondPublisher() throws IOException, XMLStreamException {

        OutputStream outputStream = FileUtils
                .getOutputStream(TEST_OUTPUT_XML_DOCS_PATH + "2nd-pub-voc-separation.xml");

        InputStream inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH +
                "sample-vocabulary-02.rtf");
        IRtfSource source = new RtfStreamSource(inputStream);
        IRtfParser parser = new StandardRtfParser();

        // Separate vocabulary parts
        VocabularySeparator vocabularySeparator = new VocabularySeparator(outputStream);
        parser.parse(source, vocabularySeparator);

        // Make the first pass to identify publisher
        PublisherIdentifier publisherIdentifier = new PublisherIdentifier(registry, vocabularySeparator);
        publisherIdentifier.identifyPublisher();

        // Make the second pass to recognize vocabulary structure
        VocabularyRecognizer vocabularyRecognizer = new VocabularyRecognizer(registry, inventoryService);
        vocabularyRecognizer.recognizeVocabulary(vocabularySeparator.streamOfVocPartsValues());

        Assertions.assertThat(inventoryService.inventorySize()).isEqualTo(5);
        Assertions.assertThat(inventoryService.isInventoried("incandescent")).isTrue();
        Assertions.assertThat(inventoryService.isInventoried("fluorescent")).isTrue();
        Assertions.assertThat(inventoryService.isInventoried("prolonged")).isTrue();
        Assertions.assertThat(inventoryService.isInventoried("shade")).isTrue();
        Assertions.assertThat(inventoryService.isInventoried("subtle")).isTrue();
    }

    @Test
    void inventoriesVocabularyForThirdPublisher() throws IOException, XMLStreamException {

        OutputStream outputStream = FileUtils
                .getOutputStream(TEST_OUTPUT_XML_DOCS_PATH + "3rd-pub-voc-separation.xml");

        InputStream inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH +
                "OurW-2b.rtf");
        IRtfSource source = new RtfStreamSource(inputStream);
        IRtfParser parser = new StandardRtfParser();

        // Separate vocabulary parts
        VocabularySeparator vocabularySeparator = new VocabularySeparator(outputStream);
        parser.parse(source, vocabularySeparator);

        // Make the first pass to identify publisher
        PublisherIdentifier publisherIdentifier = new PublisherIdentifier(registry, vocabularySeparator);
        publisherIdentifier.identifyPublisher();

        // Make the second pass to recognize vocabulary structure
        VocabularyRecognizer vocabularyRecognizer = new VocabularyRecognizer(registry, inventoryService);
        vocabularyRecognizer.recognizeVocabulary(vocabularySeparator.streamOfVocPartsValues());

        Assertions.assertThat(inventoryService.inventorySize()).isEqualTo(7);
        Assertions.assertThat(inventoryService.isInventoried("neighbour")).isTrue();
        Assertions.assertThat(inventoryService.isInventoried("lighthouse")).isTrue();
        Assertions.assertThat(inventoryService.isInventoried("keeper")).isTrue();
        Assertions.assertThat(inventoryService.isInventoried("coast")).isTrue();
        Assertions.assertThat(inventoryService.isInventoried("mean")).isTrue();
        Assertions.assertThat(inventoryService.isInventoried("tower")).isTrue();
        Assertions.assertThat(inventoryService.isInventoried("shine")).isTrue();
    }

    @Test
    void inventoriesVocabularyForThirdPublisherComplete() throws IOException, XMLStreamException {

        OutputStream outputStream = FileUtils
                .getOutputStream(TEST_OUTPUT_XML_DOCS_PATH + "3rd-pub-complete-voc-separation.xml");

        InputStream inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH +
                "OurW-2b-complete.rtf");
        IRtfSource source = new RtfStreamSource(inputStream);
        IRtfParser parser = new StandardRtfParser();

        // Separate vocabulary parts
        VocabularySeparator vocabularySeparator = new VocabularySeparator(outputStream);
        parser.parse(source, vocabularySeparator);

        // Make the first pass to identify publisher
        PublisherIdentifier publisherIdentifier = new PublisherIdentifier(registry, vocabularySeparator);
        publisherIdentifier.identifyPublisher();

        // Make the second pass to recognize vocabulary structure
        VocabularyRecognizer vocabularyRecognizer = new VocabularyRecognizer(registry, inventoryService);
        vocabularyRecognizer.recognizeVocabulary(vocabularySeparator.streamOfVocPartsValues());

        Assertions.assertThat(inventoryService.inventorySize()).isEqualTo(30);
    }

    @Test
    void inventoriesVocabularyForGePublisher() throws IOException, XMLStreamException {

        OutputStream outputStream = FileUtils
                .getOutputStream(TEST_OUTPUT_XML_DOCS_PATH + "Ge-pub-complete-voc-separation.xml");

        InputStream inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH +
                "GE-B2-b.rtf");
        IRtfSource source = new RtfStreamSource(inputStream);
        IRtfParser parser = new StandardRtfParser();

        // Separate vocabulary parts
        VocabularySeparator vocabularySeparator = new VocabularySeparator(outputStream);
        parser.parse(source, vocabularySeparator);

        // Cannot identify this publisher for the time being. Explicitly state publisher
        registry.setActiveSpec("GE");

        // Make the second pass to recognize vocabulary structure
        VocabularyRecognizer vocabularyRecognizer = new VocabularyRecognizer(registry, inventoryService);
        vocabularyRecognizer.recognizeVocabulary(vocabularySeparator.streamOfVocPartsValues());

        Assertions.assertThat(inventoryService.inventorySize()).isEqualTo(18);
    }

    @Test
    void inventoriesVocabularyForPublisherE() throws IOException, XMLStreamException {

        OutputStream outputStream = FileUtils
                .getOutputStream(TEST_OUTPUT_XML_DOCS_PATH + "E-pub-complete-voc-separation.xml");

        InputStream inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH +
                "PubE.rtf");
        IRtfSource source = new RtfStreamSource(inputStream);
        IRtfParser parser = new StandardRtfParser();

        // Separate vocabulary parts
        VocabularySeparator vocabularySeparator = new VocabularySeparator(outputStream);
        parser.parse(source, vocabularySeparator);

        // Skip identification for now
        registry.setActiveSpec("Pub-E");

        // Make the second pass to recognize vocabulary structure
        VocabularyRecognizer vocabularyRecognizer = new VocabularyRecognizer(registry, inventoryService);
        vocabularyRecognizer.recognizeVocabulary(vocabularySeparator.streamOfVocPartsValues());

        Assertions.assertThat(inventoryService.inventorySize()).isEqualTo(37);
    }
}

