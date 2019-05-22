package edu.teikav.robot.parser.domain;

import java.util.List;
import java.util.Map;

public class PublisherDocumentInput {

    private Publisher publisher;
    private List<VocabularyTokenSpecs> vocabularyStructureSpecs;
    private List<String> vocabularyStructureTransitions;
    private Map<String, SpeechPart> vocabularySpeechPartMappings;

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public List<VocabularyTokenSpecs> getVocabularyStructureSpecs() {
        return vocabularyStructureSpecs;
    }

    public void setVocabularyStructureSpecs(List<VocabularyTokenSpecs> vocabularyStructureSpecs) {
        this.vocabularyStructureSpecs = vocabularyStructureSpecs;
    }

    public List<String> getVocabularyStructureTransitions() {
        return vocabularyStructureTransitions;
    }

    public void setVocabularyStructureTransitions(List<String> vocabularyStructureTransitions) {
        this.vocabularyStructureTransitions = vocabularyStructureTransitions;
    }

    public Map<String, SpeechPart> getVocabularySpeechPartMappings() {
        return vocabularySpeechPartMappings;
    }

    public void setVocabularySpeechPartMappings(Map<String, SpeechPart> vocabularySpeechPartMappings) {
        this.vocabularySpeechPartMappings = vocabularySpeechPartMappings;
    }
}
