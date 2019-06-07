package edu.teikav.robot.parser.domain;

import java.util.Objects;

public class InventoryItem {

    private String term;
    private SpeechPart termType;
    private String translation;
    private String example;
    private String pronunciation;
    private String derivative;
    private String opposite;
    private String verbParticiples;

    public InventoryItem(String term) {
        this.term = term;
    }

    public InventoryItem(InventoryItem item) {
        this.term = item.getTerm();
        this.termType = item.getTermType();
        this.translation = item.getTranslation();
        this.example = item.getExample();
        this.pronunciation = item.getPronunciation();
        this.derivative = item.getDerivative();
        this.opposite = item.getOpposite();
        this.verbParticiples = item.getVerbParticiples();
    }

    public void setTermType(SpeechPart termType) {
        this.termType = termType;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getTerm() {
        return term;
    }

    public SpeechPart getTermType() {
        return termType;
    }

    public String getTranslation() {
        return translation;
    }

    public String getExample() {
        return example;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    public String getDerivative() {
        return derivative;
    }

    public void setDerivative(String derivative) {
        this.derivative = derivative;
    }

    public String getOpposite() { return opposite; }

    public void setOpposite(String opposite) { this.opposite = opposite; }

    public String getVerbParticiples() {
        return verbParticiples;
    }

    public void setVerbParticiples(String verbParticiples) {
        this.verbParticiples = verbParticiples;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryItem that = (InventoryItem) o;
        return Objects.equals(term, that.term) &&
                termType == that.termType &&
                Objects.equals(translation, that.translation) &&
                Objects.equals(example, that.example) &&
                Objects.equals(pronunciation, that.pronunciation) &&
                Objects.equals(derivative, that.derivative) &&
                Objects.equals(opposite, that.opposite) &&
                Objects.equals(verbParticiples, that.verbParticiples);
    }

    @Override
    public int hashCode() {

        return Objects.hash(term, termType.toString(), translation, example,
                pronunciation, derivative, opposite, verbParticiples);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("InventoryItem{");
        sb.append("term='").append(term).append('\'');
        sb.append(", termType=").append(termType);
        if (pronunciation != null && pronunciation.length() > 0) {
            sb.append(", pronunciation=").append(pronunciation);
        }
        sb.append(", translation='").append(translation).append('\'');
        if (example != null && example.length() > 0) {
            sb.append(", example='").append(example).append('\'');
        }
        if (derivative != null && derivative.length() > 0) {
            sb.append(", derivative='").append(derivative).append('\'');
        }
        if (opposite != null && opposite.length() > 0) {
            sb.append(", opposite='").append(opposite).append('\'');
        }
        if (verbParticiples != null && verbParticiples.length() > 0) {
            sb.append(", verbParticiples='").append(verbParticiples).append('\'');
        }
        sb.append('}');
        return sb.toString();
    }
}
