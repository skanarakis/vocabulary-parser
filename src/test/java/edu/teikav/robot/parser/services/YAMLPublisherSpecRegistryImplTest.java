package edu.teikav.robot.parser.services;

import edu.teikav.robot.parser.domain.Publisher;
import edu.teikav.robot.parser.domain.PublisherDocumentInput;
import edu.teikav.robot.parser.domain.PublisherSpecification;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.util.Optional;

public class YAMLPublisherSpecRegistryImplTest {

    private PublisherSpecificationRegistry registry;

    @Before
    public void initialize() {
        registry = new YAMLPublisherSpecRegistryImpl(
                new Yaml(new Constructor(PublisherDocumentInput.class)));
    }

    @Test
    public void loadSinglePublisherSpec() {

        InputStream publisherSpecStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("publishers/one-publisher.yaml");
        registry.registerPublisherSpecification(publisherSpecStream);
        Assertions.assertThat(registry.registrySize()).isEqualTo(1);
    }

    @Test
    public void loadTwoPublisherSpecs() {

        InputStream publisherSpecStreams = this.getClass()
                .getClassLoader()
                .getResourceAsStream("publishers/two-publishers.yaml");
        registry.registerPublisherSpecifications(publisherSpecStreams);
        Assertions.assertThat(registry.registrySize()).isEqualTo(2);
    }

    @Test
    public void loadAllPublisherSpecs() {

        InputStream publisherSpecStreams = this.getClass()
                .getClassLoader()
                .getResourceAsStream("publishers/all-publishers.yaml");
        registry.registerPublisherSpecifications(publisherSpecStreams);
        Assertions.assertThat(registry.registrySize()).isEqualTo(4);
    }

    @Test
    public void loadedSpecShouldReflectYAMLFile() {

        InputStream publisherSpecStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("publishers/one-publisher.yaml");
        registry.registerPublisherSpecification(publisherSpecStream);
        Assertions.assertThat(registry.registrySize()).isEqualTo(1);

        String publisherName = "Publisher A";
        String publisherDescription = "PubA Description";
        String publicationDate = "2018";
        Publisher publisher = new Publisher(publisherName, publisherDescription, publicationDate);

        Optional<PublisherSpecification> optSpec = registry.findSpecByPublisher(publisher);
        Assertions.assertThat(optSpec.isPresent()).isTrue();
        PublisherSpecification spec = optSpec.orElseThrow(() -> new RuntimeException("No spec retrieved"));

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(spec.getPublisher()).isEqualTo(publisher);
        softAssertions.assertThat(spec.getSpecHashCodes().size()).isEqualTo(2);
        softAssertions.assertThat(spec.getFormatSpecsOfVocabularyTerms().size()).isEqualTo(4);
        softAssertions.assertAll();
    }
}