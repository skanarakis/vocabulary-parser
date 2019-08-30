package edu.teikav.robot.parser.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class InventoryItem {

    private final String term;
    private SpeechPart termType;
    private String translation;
    private String example;
    private String pronunciation;
    private String derivative;
    private String opposite;
    private String verbParticiples;
    private String phrase;
    private String synonyms;

    public static class Builder {
        // Required
        private final String term;

        // Optional
        private SpeechPart termType;
        private String translation;
        private String example;
        private String pronunciation;
        private String derivative;
        private String opposite;
        private String verbParticiples;
        private String phrase;
        private String synonyms;

        public Builder(String term) {
            this.term = term;
        }

        public Builder ofType(SpeechPart termType) {
            this.termType = termType;
            return this;
        }

        public Builder translatedAs(String translation) {
            this.translation = translation;
            return this;
        }

        public Builder havingExample(String example) {
            this.example = example;
            return this;
        }

        public Builder pronouncedAs(String pronunciation) {
            this.pronunciation = pronunciation;
            return this;
        }

        public Builder withDerivate(String derivate) {
            this.derivative = derivate;
            return this;
        }

        public Builder withOpposite(String opposite) {
            this.opposite = opposite;
            return this;
        }

        public Builder withVerbParticiples(String verbParticiples) {
            this.verbParticiples = verbParticiples;
            return this;
        }

        public Builder havingSynonym(String synonyms) {
            this.synonyms = synonyms;
            return this;
        }

        public Builder usedInPhrasesLike(String phrase) {
            this.phrase = phrase;
            return this;
        }

        public InventoryItem build() {
            return new InventoryItem(this);
        }
    }

    private InventoryItem(Builder builder) {
        this.term = builder.term;
        this.termType = builder.termType;
        this.translation = builder.translation;
        this.example = builder.example;
        this.pronunciation = builder.pronunciation;
        this.derivative = builder.derivative;
        this.opposite = builder.opposite;
        this.verbParticiples = builder.verbParticiples;
        this.phrase = builder.phrase;
        this.synonyms = builder.synonyms;
    }

    public static InventoryItem createEmptyItemFor(String term) {
        return new Builder(term).build();
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
        this.phrase = item.getPhrase();
        this.synonyms = item.getSynonyms();
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
        if (phrase != null && phrase.length() > 0) {
            sb.append(", phrase='").append(phrase).append('\'');
        }
        if (synonyms != null && synonyms.length() > 0) {
            sb.append(", synonyms='").append(synonyms).append('\'');
        }
        sb.append('}');
        return sb.toString();
    }
}