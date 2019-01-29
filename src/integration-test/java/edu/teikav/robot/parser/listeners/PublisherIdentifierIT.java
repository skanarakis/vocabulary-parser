package edu.teikav.robot.parser.listeners;

import static edu.teikav.robot.parser.ParserStaticConstants.FIRST_PASS_TEST_OUTPUT_XML_FILENAME;
import static edu.teikav.robot.parser.ParserStaticConstants.TEST_INPUT_RTF_DOCS_PATH;
import static edu.teikav.robot.parser.ParserStaticConstants.TEST_OUTPUT_XML_DOCS_PATH;
import static org.mockito.ArgumentMatchers.any;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

import javax.xml.stream.XMLStreamException;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.rtfparserkit.parser.IRtfListener;
import com.rtfparserkit.parser.IRtfParser;
import com.rtfparserkit.parser.IRtfSource;
import com.rtfparserkit.parser.RtfStreamSource;
import com.rtfparserkit.parser.standard.StandardRtfParser;
import com.rtfparserkit.utils.RtfDumpListener;

import edu.teikav.robot.parser.FileUtils;
import edu.teikav.robot.parser.IntegrationTest;
import edu.teikav.robot.parser.domain.PublisherGrammar;
import edu.teikav.robot.parser.domain.PublisherGrammarContext;
import edu.teikav.robot.parser.services.PublisherGrammarRegistry;
import edu.teikav.robot.parser.services.YAMLBasedPublisherGrammarRegistryImpl;

@RunWith(SpringRunner.class)
@Category(IntegrationTest.class)
public class PublisherIdentifierIT {

    @MockBean
    private PublisherGrammarRegistry grammarRegistry;

    @Autowired
    private OutputStream outputStream;

    @Autowired
    private Yaml yaml;

    @Test
    public void identifyGrammar_mockedRegistry() throws IOException, XMLStreamException {

        PublisherGrammarContext context = Mockito.mock(PublisherGrammarContext.class);
        Optional<PublisherGrammarContext> contextOptional = Optional.of(context);
        Mockito.when(grammarRegistry.findGrammar(any(Integer.class)))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.empty())
                .thenReturn(Optional.empty())
                .thenReturn(Optional.empty())
                .thenReturn(contextOptional);
        Mockito.doNothing().when(grammarRegistry).setActiveGrammarContext(any(PublisherGrammarContext.class));

        InputStream inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH + "sample-vocabulary-01.rtf");
        IRtfSource source = new RtfStreamSource(inputStream);
        IRtfParser parser = new StandardRtfParser();
        IRtfListener firstPassListener = new PublisherIdentifier(grammarRegistry, outputStream);
        parser.parse(source, firstPassListener);

        Mockito.verify(grammarRegistry, Mockito.times(5)).findGrammar(any(Integer.class));
        Mockito.verify(grammarRegistry, Mockito.times(1)).setActiveGrammarContext(context);
    }

    @Test
    public void identifyGrammar_whenRealRegistry_hasOneRegisteredGrammar()
            throws IOException, XMLStreamException {

        PublisherGrammarRegistry registry = new YAMLBasedPublisherGrammarRegistryImpl(yaml);
        PublisherGrammarRegistry spiedRegistry = Mockito.spy(registry);

        InputStream publishersInputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("publishers/one-publisher.yaml");
        spiedRegistry.loadSingleGrammar(publishersInputStream);
        Assertions.assertThat(spiedRegistry.numberOfGrammars()).isEqualTo(1);

        InputStream inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH + "sample-vocabulary-01.rtf");
        IRtfSource source = new RtfStreamSource(inputStream);
        IRtfParser parser = new StandardRtfParser();
        IRtfListener firstPassListener = new PublisherIdentifier(spiedRegistry, outputStream);
        parser.parse(source, firstPassListener);

        Mockito.verify(spiedRegistry, Mockito.times(4)).findGrammar(any(Integer.class));
    }

    @Test
    public void identifyGrammarOfFirstPublisher_whenRealRegistry_hasTwoRegisteredGrammars()
            throws IOException, XMLStreamException {

        PublisherGrammarRegistry registry = new YAMLBasedPublisherGrammarRegistryImpl(yaml);
        PublisherGrammarRegistry spiedRegistry = Mockito.spy(registry);

        InputStream publishersInputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("publishers/two-publishers.yaml");
        spiedRegistry.loadMultipleGrammars(publishersInputStream);
        Assertions.assertThat(spiedRegistry.numberOfGrammars()).isEqualTo(2);

        InputStream inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH + "sample-vocabulary-01.rtf");
        IRtfSource source = new RtfStreamSource(inputStream);
        IRtfParser parser = new StandardRtfParser();
        IRtfListener firstPassListener = new PublisherIdentifier(spiedRegistry, outputStream);
        parser.parse(source, firstPassListener);

        Mockito.verify(spiedRegistry, Mockito.times(4)).findGrammar(any(Integer.class));
    }

    @Test
    public void identifyGrammarOfFirstPublisher_whenRealRegistry_hasManyRegisteredGrammars()
            throws IOException, XMLStreamException {

        PublisherGrammarRegistry registry = new YAMLBasedPublisherGrammarRegistryImpl(yaml);
        PublisherGrammarRegistry spiedRegistry = Mockito.spy(registry);

        InputStream publishersInputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("publishers/all-publishers.yaml");
        spiedRegistry.loadMultipleGrammars(publishersInputStream);

        InputStream inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH + "sample-vocabulary-01.rtf");
        IRtfSource source = new RtfStreamSource(inputStream);
        IRtfParser parser = new StandardRtfParser();
        IRtfListener firstPassListener = new PublisherIdentifier(spiedRegistry, outputStream);
        parser.parse(source, firstPassListener);

        Mockito.verify(spiedRegistry, Mockito.times(4)).findGrammar(any(Integer.class));
    }

    @Test
    public void identifyGrammarOfSecondPublisher_whenRealRegistry_hasTwoRegisteredGrammars()
            throws IOException, XMLStreamException {

        PublisherGrammarRegistry registry = new YAMLBasedPublisherGrammarRegistryImpl(yaml);
        PublisherGrammarRegistry spiedRegistry = Mockito.spy(registry);

        InputStream publishersInputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("publishers/two-publishers.yaml");
        spiedRegistry.loadMultipleGrammars(publishersInputStream);
        Assertions.assertThat(spiedRegistry.numberOfGrammars()).isEqualTo(2);

        InputStream inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH + "sample-vocabulary-02.rtf");
        IRtfSource source = new RtfStreamSource(inputStream);
        IRtfParser parser = new StandardRtfParser();
        IRtfListener firstPassListener = new PublisherIdentifier(spiedRegistry, outputStream);
        parser.parse(source, firstPassListener);

        Mockito.verify(spiedRegistry, Mockito.times(4)).findGrammar(any(Integer.class));
    }

    @Test
    public void identifyGrammarOfThirdPublisher_whenRealRegistry_hasManyRegisteredGrammars()
            throws IOException, XMLStreamException {

        PublisherGrammarRegistry registry = new YAMLBasedPublisherGrammarRegistryImpl(yaml);
        PublisherGrammarRegistry spiedRegistry = Mockito.spy(registry);

        InputStream publishersInputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("publishers/all-publishers.yaml");
        spiedRegistry.loadMultipleGrammars(publishersInputStream);

        InputStream inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH + "OurW-2b.rtf");
        IRtfSource source = new RtfStreamSource(inputStream);
        IRtfParser parser = new StandardRtfParser();
        IRtfListener firstPassListener = new PublisherIdentifier(spiedRegistry, outputStream);
        parser.parse(source, firstPassListener);

        Mockito.verify(spiedRegistry, Mockito.times(4)).findGrammar(any(Integer.class));
    }

    @Test
    public void produceXmlFromRealRtfDocument() throws IOException, XMLStreamException {
        InputStream inputStream = new FileInputStream(TEST_INPUT_RTF_DOCS_PATH + "OurW-2b.rtf");
        IRtfSource source = new RtfStreamSource(inputStream);
        IRtfParser parser = new StandardRtfParser();
        IRtfListener xmlProducer = new RtfDumpListener(outputStream);
        parser.parse(source, xmlProducer);
    }

    @TestConfiguration
    static class PublisherIdentifierTestConfiguration {

        @Bean
        OutputStream firstPassOutputStream() throws IOException {
            return
                    FileUtils.getOutputStream(TEST_OUTPUT_XML_DOCS_PATH + FIRST_PASS_TEST_OUTPUT_XML_FILENAME);
        }

        @Bean
        Yaml yaml() {
            return new Yaml(new Constructor(PublisherGrammar.class));
        }
    }

}