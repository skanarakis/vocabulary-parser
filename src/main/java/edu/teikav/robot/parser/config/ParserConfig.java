package edu.teikav.robot.parser.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
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
    public ObjectMapper objectMapper() {
        return new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Bean
    public ObjectWriter objectWriter(ObjectMapper objectMapper) {
        return objectMapper.writerWithDefaultPrettyPrinter();
    }

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
