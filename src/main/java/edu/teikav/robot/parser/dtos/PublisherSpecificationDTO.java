package edu.teikav.robot.parser.dtos;

import edu.teikav.robot.parser.domain.Publisher;
import edu.teikav.robot.parser.domain.PublisherSpecification;
import edu.teikav.robot.parser.domain.SpeechPart;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PublisherSpecificationDTO {

    private Publisher publisher;
    private Set<Integer> specHashCodes;
    private List<VocabularyTokenSpecsDTO> vocabularyTokenSpecs;
    private Map<String, SpeechPart> speechPartsMap;

    public PublisherSpecificationDTO() {}

    public PublisherSpecificationDTO(PublisherSpecification specification) {


        publisher = specification.getPublisher();
        specHashCodes = specification.getSpecHashCodes();

        vocabularyTokenSpecs = new ArrayList<>();
        specification.getFormatSpecsOfVocabularyTerms()
                .forEach(spec -> vocabularyTokenSpecs.add(new VocabularyTokenSpecsDTO(spec)));

        speechPartsMap = specification.getSpeechPartsMap();
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public Set<Integer> getSpecHashCodes() {
        return specHashCodes;
    }

    public void setSpecHashCodes(Set<Integer> specHashCodes) {
        this.specHashCodes = specHashCodes;
    }

    public List<VocabularyTokenSpecsDTO> getVocabularyTokenSpecs() {
        return vocabularyTokenSpecs;
    }

    public void setVocabularyTokenSpecs(List<VocabularyTokenSpecsDTO> vocabularyTokenSpecs) {
        this.vocabularyTokenSpecs = vocabularyTokenSpecs;
    }

    public Map<String, SpeechPart> getSpeechPartsMap() {
        return speechPartsMap;
    }

    public void setSpeechPartsMap(Map<String, SpeechPart> speechPartsMap) {
        this.speechPartsMap = speechPartsMap;
    }
}
