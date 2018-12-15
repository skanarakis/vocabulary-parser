package edu.teikav.robot.parser.dtos;

import edu.teikav.robot.parser.domain.Publisher;
import edu.teikav.robot.parser.domain.PublisherGrammarContext;
import edu.teikav.robot.parser.domain.VocabularyToken;

import java.util.List;

public class PublisherGrammarContextDTO {

    private Publisher publisher;
    private int grammarHashCode;
    private List<String> tokens;
    private List<VocabularyToken> tokenSpecs;

    public PublisherGrammarContextDTO() {}

    public PublisherGrammarContextDTO(PublisherGrammarContext grammarContext) {
        publisher = grammarContext.getPublisher();
        tokens = grammarContext.vocabularyOrdering();
        tokenSpecs = grammarContext.getTokenSpecs();
        grammarHashCode = grammarContext.getGrammarHashCode();
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public int getGrammarHashCode() {
        return grammarHashCode;
    }

    public void setGrammarHashCode(int grammarHashCode) {
        this.grammarHashCode = grammarHashCode;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
    }

    public List<VocabularyToken> getTokenSpecs() {
        return tokenSpecs;
    }

    public void setTokenSpecs(List<VocabularyToken> tokenSpecs) {
        this.tokenSpecs = tokenSpecs;
    }
}
