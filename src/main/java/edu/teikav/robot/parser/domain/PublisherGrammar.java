package edu.teikav.robot.parser.domain;

import java.util.List;
import java.util.Map;

public class PublisherGrammar {

    private Publisher publisher;
    private String vocabularyTokensOrdering;
    private List<VocabularyToken> vocabularyTokenSpecs;
    private Map<String, TermGrammarTypes> grammarTermTypes;

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public String getVocabularyTokensOrdering() {
        return vocabularyTokensOrdering;
    }

    public void setVocabularyTokensOrdering(String vocabularyTokensOrdering) {
        this.vocabularyTokensOrdering = vocabularyTokensOrdering;
    }

    public List<VocabularyToken> getVocabularyTokenSpecs() {
        return vocabularyTokenSpecs;
    }

    public void setVocabularyTokenSpecs(List<VocabularyToken> vocabularyTokenSpecs) {
        this.vocabularyTokenSpecs = vocabularyTokenSpecs;
    }

    public Map<String, TermGrammarTypes> getGrammarTermTypes() {
        return grammarTermTypes;
    }

    public void setGrammarTermTypes(Map<String, TermGrammarTypes> grammarTermTypes) {
        this.grammarTermTypes = grammarTermTypes;
    }
}
