package edu.teikav.robot.parser.dtos;

import edu.teikav.robot.parser.domain.InventoryItem;
import edu.teikav.robot.parser.domain.SpeechPart;
import edu.teikav.robot.parser.util.GenericUtils;

public class InventoryItemDTO {

    private SpeechPart speechPart;
    private String translation;
    private String example;
    private String derivative;
    private String opposite;
    private String pronunciation;
    private String verbParticiples;
    private String phrase;
    private String synonyms;


    public InventoryItemDTO() {}

    public InventoryItemDTO(InventoryItem item) {

        if (item == null) {
            throw new NullPointerException("Null inventory item passed");
        }
        GenericUtils.validate(item.getTerm(), "Term cannot be null");

        speechPart = item.getTermType();
        translation = item.getTranslation();
        example = item.getExample();
        derivative = item.getDerivative();
        opposite = item.getOpposite();
        pronunciation = item.getPronunciation();
        verbParticiples = item.getVerbParticiples();
        phrase = item.getPhrase();
        synonyms = item.getSynonyms();
    }

    public SpeechPart getSpeechPart() {
        return speechPart;
    }

    public void setSpeechPart(SpeechPart speechPart) {
        this.speechPart = speechPart;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getDerivative() {
        return derivative;
    }

    public void setDerivative(String derivative) {
        this.derivative = derivative;
    }

    public String getOpposite() { return opposite; }

    public void setOpposite(String opposite) { this.opposite = opposite; }

    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    public String getVerbParticiples() {
        return verbParticiples;
    }

    public void setVerbParticiples(String verbParticiples) {
        this.verbParticiples = verbParticiples;
    }

    public String getPhrase() { return phrase; }

    public void setPhrase(String phrase) { this.phrase = phrase; }

    public String getSynonyms() { return synonyms; }

    public void setSynonyms(String synonyms) { this.synonyms = synonyms; }
}
