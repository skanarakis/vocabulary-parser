package edu.teikav.robot.parser.config;

import edu.teikav.robot.parser.domain.PublisherGrammar;
import edu.teikav.robot.parser.services.PublisherGrammarRegistry;
import edu.teikav.robot.parser.services.YAMLBasedPublisherGrammarRegistryImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import static edu.teikav.robot.parser.ParserStaticConstants.*;

@Configuration
@Profile("dev")
public class ParserConfig {

    @Bean
    @Qualifier("FirstPassOutputStream")
    OutputStream firstPassOutputStream() throws FileNotFoundException {
        return new FileOutputStream(TEST_OUTPUT_XML_DOCS_PATH + FIRST_PASS_OUTPUT_XML_FILENAME);
    }

    @Bean
    @Qualifier("SecondPassOutputStream")
    OutputStream secondPassOutputStream() throws FileNotFoundException {
        return new FileOutputStream(TEST_OUTPUT_XML_DOCS_PATH + SECOND_PASS_OUTPUT_XML_FILENAME);
    }

    @Bean
    PublisherGrammarRegistry registry() {
        Yaml yaml = new Yaml(new Constructor(PublisherGrammar.class));
        return new YAMLBasedPublisherGrammarRegistryImpl(yaml);
    }

}
