package edu.teikav.robot.parser.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public List<VocabularyToken> getTokenSpecs() {
        return tokenSpecs;
    }

    public boolean isPartOfTermOptional(String type) {
        return false;
    }

    public List<String> getStructureRelations() {
        return null;
    }

    public String patternOfToken(String type) {
        return "";
    }

    public boolean isPartPotentiallyLast(String type) {
        return false;
    }

    public boolean isPartPotentiallyComposite(String type) {
        return false;
    }

    public List<String> getCompositePartsFor(String type) {
        return null;
    }

    public String getCompositePartSplitToken(String type) {
        return null;
    }

    public boolean isPartPotentiallySplit(String type) {
        return false;
    }

    // helper methods to allow clients check their next token according to grammar rules
}
