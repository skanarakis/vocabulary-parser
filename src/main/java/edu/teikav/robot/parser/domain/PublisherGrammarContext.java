package edu.teikav.robot.parser.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class PublisherGrammarContext {

    private Logger logger = LoggerFactory.getLogger(PublisherGrammarContext.class);

    private int grammarID;
    private Publisher publisher;
    private String[] vocabularyPartsOrdering;
    private int grammarHashCode;
    private List<VocabularyToken> tokenSpecs;
    private Map<String, TermGrammarTypes> grammarTypes;

    public PublisherGrammarContext(int grammarID, PublisherGrammar grammar) {

        tokenSpecs = new ArrayList<>();
        grammarTypes = new HashMap<>();

        this.grammarID = grammarID;
        publisher = grammar.getPublisher();

        vocabularyPartsOrdering = grammar.getVocabularyTokensOrdering().split(" ");

        grammar.getVocabularyTokenSpecs().forEach(t -> tokenSpecs.add(t.clone()));
        grammarHashCode = Objects.hash(tokenSpecs);

        grammarTypes = grammar.getGrammarTermTypes();
    }

    public int getGrammarID() {
        return grammarID;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public int getGrammarHashCode() {
        return grammarHashCode;
    }

    public int numberOfVocabularyDifferentParts() {
        return vocabularyPartsOrdering.length;
    }

    public List<String> vocabularyOrdering() {
        return Arrays.asList(vocabularyPartsOrdering);
    }

    public Optional<TermGrammarTypes> getGrammarTypeFor(String input) {
        TermGrammarTypes grammarType = grammarTypes.get(input);
        if (grammarType == null) {
            return Optional.empty();
        }
        return Optional.of(grammarType);
    }

    // helper methods to allow clients check their next token according to grammar rules
}
