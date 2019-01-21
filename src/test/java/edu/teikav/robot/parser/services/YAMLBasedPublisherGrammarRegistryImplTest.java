package edu.teikav.robot.parser.services;

import edu.teikav.robot.parser.domain.Publisher;
import edu.teikav.robot.parser.domain.PublisherGrammar;
import edu.teikav.robot.parser.domain.PublisherGrammarContext;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.util.Arrays;

@RunWith(SpringRunner.class)
public class YAMLBasedPublisherGrammarRegistryImplTest {

    @Autowired
    private Yaml yaml;

    private PublisherGrammarRegistry registry;

    @Before
    public void initialize() {
        registry = new YAMLBasedPublisherGrammarRegistryImpl(yaml);
    }

    @Test
    public void loadSingleGrammar() {

        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("publishers/one-publisher.yaml");
        registry.loadSingleGrammar(inputStream);
        Assertions.assertThat(registry.numberOfGrammars()).isEqualTo(1);
    }

    @Test
    public void loadTwoGrammars() {

        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("publishers/two-publishers.yaml");
        registry.loadMultipleGrammars(inputStream);
        Assertions.assertThat(registry.numberOfGrammars()).isEqualTo(2);
    }

    @Test
    public void loadAllGrammars() {

        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("publishers/all-publishers.yaml");
        registry.loadMultipleGrammars(inputStream);
        Assertions.assertThat(registry.numberOfGrammars()).isEqualTo(3);
    }

    @Test
    public void loadedGrammarShouldReflectYAMLFile() {

        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("publishers/one-publisher.yaml");
        registry.loadSingleGrammar(inputStream);
        Assertions.assertThat(registry.numberOfGrammars()).isEqualTo(1);

        String publisherName = "Publisher A";
        String publisherDescription = "PubA Description";
        String publicationDate = "2018";
        Publisher publisher = new Publisher(publisherName, publisherDescription, publicationDate);

        PublisherGrammarContext context = registry.getPublisherGrammar(publisher);
        Assertions.assertThat(context).isNotNull();

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(context.numberOfVocabularyDifferentParts()).isEqualTo(5);
        softAssertions.assertThat(context.getGrammarHashCode()).isEqualTo(2045894956);
        String[] ordering = {"GARBAGE", "TERM", "GRAMMAR_TYPE", "TRANSLATION", "EXAMPLE"};
        softAssertions.assertThat(context.vocabularyOrdering()).isEqualTo(Arrays.asList(ordering));
        softAssertions.assertThat(context.getPublisher()).isEqualTo(publisher);
        softAssertions.assertAll();
    }

    @TestConfiguration
    static class PublisherGrammarTestConfiguration {

        @Bean
        public Yaml getYaml() {
           return new Yaml(new Constructor(PublisherGrammar.class));
        }
    }
}