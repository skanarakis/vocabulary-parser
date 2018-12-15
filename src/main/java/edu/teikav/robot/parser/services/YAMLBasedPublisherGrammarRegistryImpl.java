package edu.teikav.robot.parser.services;

import edu.teikav.robot.parser.domain.Publisher;
import edu.teikav.robot.parser.domain.PublisherGrammar;
import edu.teikav.robot.parser.domain.PublisherGrammarContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class YAMLBasedPublisherGrammarRegistryImpl implements PublisherGrammarRegistry {

    private Logger logger = LoggerFactory.getLogger(YAMLBasedPublisherGrammarRegistryImpl.class);

    private Yaml yaml;
    private Map<Integer, PublisherGrammar> grammars;
    private Map<Integer, PublisherGrammarContext> grammarContexts;
    private AtomicInteger nextID;
    private PublisherGrammarContext activeGrammarContext;

    public YAMLBasedPublisherGrammarRegistryImpl(Yaml yaml) {
        this.yaml = yaml;
        this.grammars = new HashMap<>();
        this.grammarContexts = new HashMap<>();
        nextID = new AtomicInteger(0);
    }

    @Override
    public Optional<PublisherGrammarContext> findGrammar(int vocabularyStructureHashCode) {
        if (grammarContexts.size() == 0) {
            return Optional.empty();
        }
        return grammarContexts.entrySet().stream()
                .filter(e -> e.getValue().getGrammarHashCode() == vocabularyStructureHashCode)
                .map(Map.Entry::getValue)
                .findFirst();
    }

    @Override
    public void setActiveGrammarContext(PublisherGrammarContext grammarContext) {
        activeGrammarContext = grammarContext;
    }

    @Override
    public Optional<PublisherGrammarContext> getActiveGrammarContext() {
        if (activeGrammarContext == null) {
            return Optional.empty();
        }
        return Optional.of(activeGrammarContext);
    }

    @Override
    public void loadSingleGrammar(InputStream inputStream){
        PublisherGrammar grammar = yaml.load(inputStream);
        loadContext(nextID.incrementAndGet(), grammar);
    }

    @Override
    public void loadMultipleGrammars(InputStream inputStream) {
        yaml.loadAll(inputStream)
                .forEach(g-> loadContext(nextID.incrementAndGet(), (PublisherGrammar)g));
    }

    @Override
    public int numberOfGrammars() {
        if (grammarContexts.size() != grammars.size()) {
            throw new RuntimeException("Registry Inconsistency");
        }
        return grammars.size();
    }

    @Override
    public PublisherGrammarContext getPublisherGrammar(Publisher publisher) {
        if (grammars.size() == 0)
            return null;
        Optional<Map.Entry<Integer, PublisherGrammar>> grammar = grammars.entrySet().stream()
                .filter(e -> e.getValue().getPublisher().equals(publisher))
                .findFirst();
        return grammar.map(e -> grammarContexts.get(e.getKey())).orElse(null);
    }

    @Override
    public List<PublisherGrammarContext> getGrammarContexts() {
        return new ArrayList<>(grammarContexts.values());
    }

    @Override
    public void clean() {
        int grammarSize = grammars.size();
        grammars.clear();
        grammarContexts.clear();
        logger.info("Cleared {} grammars from Registry", grammarSize);
    }

    private void loadContext(int grammarID, PublisherGrammar grammar) {
        grammars.putIfAbsent(grammarID, grammar);
        grammarContexts.putIfAbsent(grammarID,
                new PublisherGrammarContext(grammarID, grammar));
    }
}
