package edu.teikav.robot.parser.config;

import edu.teikav.robot.parser.domain.PublisherDocumentInput;
import edu.teikav.robot.parser.services.InMemoryInventoryServiceImpl;
import edu.teikav.robot.parser.services.InventoryService;
import edu.teikav.robot.parser.services.PublisherSpecificationRegistry;
import edu.teikav.robot.parser.services.YAMLPublisherSpecRegistryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

@Configuration
@Profile("dev")
public class ParserConfig {

    @Bean
    PublisherSpecificationRegistry registry() {
        Yaml yaml = new Yaml(new Constructor(PublisherDocumentInput.class));
        return new YAMLPublisherSpecRegistryImpl(yaml);
    }

    @Bean
    InventoryService inventoryService() {
        return new InMemoryInventoryServiceImpl();
    }

}
