package edu.teikav.robot.parser.services;

import edu.teikav.robot.parser.domain.Publisher;
import edu.teikav.robot.parser.domain.PublisherGrammarContext;

import java.io.InputStream;
import java.util.Optional;

public interface PublisherGrammarRegistry {

    Optional<PublisherGrammarContext> findGrammar(int vocabularyStructureHashCode);

    void setActiveGrammarContext(PublisherGrammarContext grammarContext);

    Optional<PublisherGrammarContext> getActiveGrammarContext();

    PublisherGrammarContext getPublisherGrammar(Publisher publisher);

    void loadSingleGrammar(InputStream inputStream);

    void loadMultipleGrammars(InputStream inputStream);

    int numberOfGrammars();

    void clean();
}
