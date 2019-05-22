package edu.teikav.robot.parser.services;

import edu.teikav.robot.parser.domain.Publisher;
import edu.teikav.robot.parser.domain.PublisherDocumentInput;
import edu.teikav.robot.parser.domain.PublisherSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;

public class YAMLPublisherSpecRegistryImpl implements PublisherSpecificationRegistry {

    private Logger logger = LoggerFactory.getLogger(YAMLPublisherSpecRegistryImpl.class);

    private Yaml yaml;
    private Set<PublisherSpecification> specs;
    private PublisherSpecification activeSpec;

    public YAMLPublisherSpecRegistryImpl(Yaml yaml) {
        this.yaml = yaml;
        this.specs = new HashSet<>();
    }

    @Override
    public void registerPublisherSpecification(InputStream inputStream){
        PublisherDocumentInput publisherDocument = yaml.load(inputStream);
        loadContext(publisherDocument);
    }

    @Override
    public void registerPublisherSpecifications(InputStream inputStream) {
        yaml.loadAll(inputStream)
                .forEach(doc -> loadContext((PublisherDocumentInput)doc));
    }

    @Override
    public Optional<PublisherSpecification> findSpecByHashCode(int specHashCode) {
        if (specs.isEmpty()) {
            return Optional.empty();
        }
        return specs.stream()
                .filter(spec -> spec.matchesAnyOfHashCodes(specHashCode))
                .findFirst();
    }

    @Override
    public Optional<PublisherSpecification> findSpecByPublisher(Publisher publisher) {
        if (specs.isEmpty())
            return Optional.empty();
        return specs.stream()
                .filter(e -> e.getPublisher().equals(publisher))
                .findFirst();
    }

    @Override
    public void setActiveSpec(PublisherSpecification spec) {
        activeSpec = spec;
    }

    @Override
    public Optional<PublisherSpecification> getActiveSpec() {
        if (activeSpec == null) {
            return Optional.empty();
        }
        return Optional.of(activeSpec);
    }

    @Override
    public int registrySize() {
        return specs.size();
    }

    @Override
    public List<PublisherSpecification> getAllSpecs() {
        return new ArrayList<>(specs);
    }

    @Override
    public void removeAllPublisherSpecs() {
        int specsRegistrySize = specs.size();
        specs.clear();
        logger.info("Cleared {} publisher specifications from Registry", specsRegistrySize);
    }

    private void loadContext(PublisherDocumentInput document) {
        specs.add(new PublisherSpecification(document));
    }
}