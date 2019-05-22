package edu.teikav.robot.parser.controllers;

import edu.teikav.robot.parser.dtos.PublisherSpecificationDTO;
import edu.teikav.robot.parser.services.PublisherSpecificationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/parser")
public class PublisherRegistryRestController {

    private Logger logger = LoggerFactory.getLogger(PublisherRegistryRestController.class);

    private PublisherSpecificationRegistry registry;

    public PublisherRegistryRestController(PublisherSpecificationRegistry registry) {
        this.registry = registry;
    }

    @PostMapping(value = "/publishers", consumes = "text/plain;charset=UTF-8")
    public void loadPublisher(@RequestBody String grammarDefinition) {

        logger.info("Uploading Publisher Grammar...");
        registry.registerPublisherSpecification(new ByteArrayInputStream(grammarDefinition.getBytes()));
    }

    @GetMapping(value = "/publishers", produces = "application/json")
    public List<PublisherSpecificationDTO> getPublishers() {

        return registry.getAllSpecs().stream()
                .map(PublisherSpecificationDTO::new).collect(Collectors.toList());
    }
}
