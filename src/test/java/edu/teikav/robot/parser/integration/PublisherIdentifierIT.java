package edu.teikav.robot.parser.integration;

import com.rtfparserkit.parser.IRtfListener;
import com.rtfparserkit.parser.IRtfParser;
import com.rtfparserkit.parser.IRtfSource;
import com.rtfparserkit.parser.RtfStreamSource;
import com.rtfparserkit.parser.standard.StandardRtfParser;
import com.rtfparserkit.utils.RtfDumpListener;
import edu.teikav.robot.parser.domain.PublisherDocumentInput;
import edu.teikav.robot.parser.domain.PublisherSpecification;
import edu.teikav.robot.parser.processors.PublisherIdentifier;
import edu.teikav.robot.parser.processors.VocabularySeparator;
import edu.teikav.robot.parser.services.PublisherSpecificationRegistry;
import edu.teikav.robot.parser.services.YAMLPublisherSpecRegistryImpl;
import edu.teikav.robot.parser.util.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import javax.xml.stream.XMLStreamException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

import static edu.teikav.robot.parser.ParserStaticConstants.TEST_INPUT_RTF_DOCS_PATH;
import static edu.teikav.robot.parser.ParserStaticConstants.TEST_OUTPUT_XML_DOCS_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@Tag("integration")
@DisplayName("Integration-Test: Publisher Identifier Module")
@ExtendWith(SpringExtension.class)
class PublisherIdentifierIT {

    @MockBean
    private PublisherSpecificationRegistry grammarRegistry;

    private static Yaml yaml;

    @BeforeAll
    static void init() {
        yaml = new Yaml(new Constructor(PublisherDocumentInput.class));
    }

    @Test
    void identifyGrammar_mockedRegistry() throws IOException, XMLStreamException {

        OutputStream outputStream = FileUtils
                .getOutputStream(TEST_OUTPUT_XML_DOCS_PATH + "identifyGrammar_mockedRegistry.xml");

        PublisherSpecification context = Mockito.mock(PublisherSpecification.class);
        Optional<PublisherSpecification> contextOptional = Optional.of(context);
        Mockito.when(grammarRegistry.findSpecByHashCode(any(Integer.class)))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.empty())
                .thenReturn(Optional.empty())
                .thenReturn(Optional.empty())
                .thenReturn(contextOptional);
        Mockito.doNothing().when(grammarRegistry).setActiveSpec(any(PublisherSpecification.class));

        InputStream inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH + "sample-vocabulary-01.rtf");
        IRtfSource source = new RtfStreamSource(inputStream);
        IRtfParser parser = new StandardRtfParser();

        VocabularySeparator vocabularySeparator = new VocabularySeparator(outputStream);
        parser.parse(source, vocabularySeparator);

        PublisherIdentifier publisherIdentifier = new PublisherIdentifier(grammarRegistry, vocabularySeparator);
        publisherIdentifier.identifyPublisher();

        Mockito.verify(grammarRegistry, Mockito.times(5)).findSpecByHashCode(any(Integer.class));
        Mockito.verify(grammarRegistry, Mockito.times(1)).setActiveSpec(context);
    }

    @Test
    void identifyGrammar_whenRealRegistry_hasOneRegisteredGrammar()
            throws IOException, XMLStreamException {

        OutputStream outputStream = FileUtils
                .getOutputStream(TEST_OUTPUT_XML_DOCS_PATH + "identifyGrammar_realRegistry-1.xml");

        PublisherSpecificationRegistry registry = new YAMLPublisherSpecRegistryImpl(yaml);
        PublisherSpecificationRegistry spiedRegistry = Mockito.spy(registry);

        InputStream publishersInputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("publishers/one-publisher.yaml");
        spiedRegistry.registerPublisherSpecification(publishersInputStream);
        assertThat(spiedRegistry.registrySize()).isEqualTo(1);

        InputStream inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH + "sample-vocabulary-01.rtf");
        IRtfSource source = new RtfStreamSource(inputStream);
        IRtfParser parser = new StandardRtfParser();

        VocabularySeparator vocabularySeparator = new VocabularySeparator(outputStream);
        parser.parse(source, vocabularySeparator);

        PublisherIdentifier publisherIdentifier = new PublisherIdentifier(spiedRegistry, vocabularySeparator);
        publisherIdentifier.identifyPublisher();

        Mockito.verify(spiedRegistry, Mockito.times(4)).findSpecByHashCode(any(Integer.class));
    }

    @Test
    void identifyGrammarOfFirstPublisher_whenRealRegistry_hasTwoRegisteredGrammars()
            throws IOException, XMLStreamException {

        OutputStream outputStream = FileUtils
                .getOutputStream(TEST_OUTPUT_XML_DOCS_PATH + "identifyGrammar_realRegistry-2.xml");

        PublisherSpecificationRegistry registry = new YAMLPublisherSpecRegistryImpl(yaml);
        PublisherSpecificationRegistry spiedRegistry = Mockito.spy(registry);

        InputStream publishersInputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("publishers/two-publishers.yaml");
        spiedRegistry.registerPublisherSpecifications(publishersInputStream);
        assertThat(spiedRegistry.registrySize()).isEqualTo(2);

        InputStream inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH + "sample-vocabulary-01.rtf");
        IRtfSource source = new RtfStreamSource(inputStream);
        IRtfParser parser = new StandardRtfParser();

        VocabularySeparator vocabularySeparator = new VocabularySeparator(outputStream);
        parser.parse(source, vocabularySeparator);

        PublisherIdentifier publisherIdentifier = new PublisherIdentifier(spiedRegistry, vocabularySeparator);
        publisherIdentifier.identifyPublisher();

        Mockito.verify(spiedRegistry, Mockito.times(4)).findSpecByHashCode(any(Integer.class));
    }

    @Test
    void identifyGrammarOfFirstPublisher_whenRealRegistry_hasManyRegisteredGrammars()
            throws IOException, XMLStreamException {

        OutputStream outputStream = FileUtils
                .getOutputStream(TEST_OUTPUT_XML_DOCS_PATH + "identifyGrammar_realRegistry-3.xml");

        PublisherSpecificationRegistry registry = new YAMLPublisherSpecRegistryImpl(yaml);
        PublisherSpecificationRegistry spiedRegistry = Mockito.spy(registry);

        InputStream publishersInputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("publishers/all-publishers.yaml");
        spiedRegistry.registerPublisherSpecifications(publishersInputStream);

        InputStream inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH + "sample-vocabulary-01.rtf");
        IRtfSource source = new RtfStreamSource(inputStream);
        IRtfParser parser = new StandardRtfParser();

        VocabularySeparator vocabularySeparator = new VocabularySeparator(outputStream);
        parser.parse(source, vocabularySeparator);

        PublisherIdentifier publisherIdentifier = new PublisherIdentifier(spiedRegistry, vocabularySeparator);
        publisherIdentifier.identifyPublisher();

        Mockito.verify(spiedRegistry, Mockito.times(4)).findSpecByHashCode(any(Integer.class));
    }

    @Test
    void identifyGrammarOfSecondPublisher_whenRealRegistry_hasTwoRegisteredGrammars()
            throws IOException, XMLStreamException {

        OutputStream outputStream = FileUtils
                .getOutputStream(TEST_OUTPUT_XML_DOCS_PATH + "identifyGrammar_realRegistry-4.xml");

        PublisherSpecificationRegistry registry = new YAMLPublisherSpecRegistryImpl(yaml);
        PublisherSpecificationRegistry spiedRegistry = Mockito.spy(registry);

        InputStream publishersInputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("publishers/two-publishers.yaml");
        spiedRegistry.registerPublisherSpecifications(publishersInputStream);
        assertThat(spiedRegistry.registrySize()).isEqualTo(2);

        InputStream inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH + "sample-vocabulary-02.rtf");
        IRtfSource source = new RtfStreamSource(inputStream);
        IRtfParser parser = new StandardRtfParser();

        VocabularySeparator vocabularySeparator = new VocabularySeparator(outputStream);
        parser.parse(source, vocabularySeparator);

        PublisherIdentifier publisherIdentifier = new PublisherIdentifier(spiedRegistry, vocabularySeparator);
        publisherIdentifier.identifyPublisher();

        Mockito.verify(spiedRegistry, Mockito.times(4)).findSpecByHashCode(any(Integer.class));
    }

    @Test
    void identifyGrammarOfThirdPublisher_whenRealRegistry_hasManyRegisteredGrammars()
            throws IOException, XMLStreamException {

        OutputStream outputStream = FileUtils
                .getOutputStream(TEST_OUTPUT_XML_DOCS_PATH + "identifyGrammar_realRegistry-5.xml");

        PublisherSpecificationRegistry registry = new YAMLPublisherSpecRegistryImpl(yaml);
        PublisherSpecificationRegistry spiedRegistry = Mockito.spy(registry);

        InputStream publishersInputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("publishers/all-publishers.yaml");
        spiedRegistry.registerPublisherSpecifications(publishersInputStream);

        InputStream inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH + "OurW-2b.rtf");
        IRtfSource source = new RtfStreamSource(inputStream);
        IRtfParser parser = new StandardRtfParser();

        VocabularySeparator vocabularySeparator = new VocabularySeparator(outputStream);
        parser.parse(source, vocabularySeparator);

        PublisherIdentifier publisherIdentifier = new PublisherIdentifier(spiedRegistry, vocabularySeparator);
        publisherIdentifier.identifyPublisher();

        Mockito.verify(spiedRegistry, Mockito.times(4)).findSpecByHashCode(any(Integer.class));
    }

    @Test
    void identifyGrammarOfGePublisher_whenRealRegistry_hasManyRegisteredGrammars()
            throws IOException, XMLStreamException {

        OutputStream outputStream = FileUtils
                .getOutputStream(TEST_OUTPUT_XML_DOCS_PATH + "identifyGrammar_realRegistry-6.xml");

        PublisherSpecificationRegistry registry = new YAMLPublisherSpecRegistryImpl(yaml);
        PublisherSpecificationRegistry spiedRegistry = Mockito.spy(registry);

        InputStream publishersInputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("publishers/all-publishers.yaml");
        spiedRegistry.registerPublisherSpecifications(publishersInputStream);

        InputStream inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH + "GE-B2-b.rtf");
        IRtfSource source = new RtfStreamSource(inputStream);
        IRtfParser parser = new StandardRtfParser();

        VocabularySeparator vocabularySeparator = new VocabularySeparator(outputStream);
        parser.parse(source, vocabularySeparator);

        PublisherIdentifier publisherIdentifier = new PublisherIdentifier(spiedRegistry, vocabularySeparator);
        publisherIdentifier.identifyPublisher();

        //Mockito.verify(spiedRegistry, Mockito.times(4)).findSpecByHashCode(any(Integer.class));
    }

    @Test
    void produceXmlFromRealRtfDocument() throws IOException, XMLStreamException {

        OutputStream outputStream = FileUtils
                .getOutputStream(TEST_OUTPUT_XML_DOCS_PATH + "test-xml-production.xml");

        InputStream inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH + "OurW-2b.rtf");
        IRtfSource source = new RtfStreamSource(inputStream);
        IRtfParser parser = new StandardRtfParser();
        IRtfListener xmlProducer = new RtfDumpListener(outputStream);
        parser.parse(source, xmlProducer);
    }
}