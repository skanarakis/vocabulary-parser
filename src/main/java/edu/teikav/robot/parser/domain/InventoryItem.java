package edu.teikav.robot.parser.domain;

public class InventoryItem {

    private String term;
    private TermGrammarTypes termType;
    private String translation;
    private String example;
    private String pronunciation;

    public InventoryItem(String term) {
        this.term = term;
    }

    public void setTermType(TermGrammarTypes termType) {
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

    public TermGrammarTypes getTermType() {
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
        sb.append('}');
        return sb.toString();
    }
}
