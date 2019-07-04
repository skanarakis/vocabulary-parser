package edu.teikav.robot.parser.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VocabularyTokenSpecs implements Cloneable {

    public static class RtfSpecs {
        private int textPoints;
        private boolean italicized;
        private boolean bold;
        private FontColor color = FontColor.BLACK;

        public int getTextPoints() {
            return textPoints;
        }

        public void setTextPoints(int textPoints) {
            this.textPoints = textPoints;
        }

        public boolean isItalicized() {
            return italicized;
        }

        public void setItalicized(boolean italicized) {
            this.italicized = italicized;
        }

        public boolean isBold() {
            return bold;
        }

        public void setBold(boolean bold) {
            this.bold = bold;
        }

        public FontColor getColor() {
            return color;
        }

        public void setColor(FontColor color) {
            this.color = color;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RtfSpecs rtfSpecs = (RtfSpecs) o;
            return getTextPoints() == rtfSpecs.getTextPoints() &&
                    isItalicized() == rtfSpecs.isItalicized() &&
                    isBold() == rtfSpecs.isBold() &&
                    getColor().equals(rtfSpecs.getColor());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getTextPoints(), isItalicized(), isBold(), getColor());
        }

        @Override
        public String toString() {
            return "RtfSpecs{" +
                    "textPoints=" + textPoints +
                    ", italicized=" + italicized +
                    ", bold=" + bold +
                    ", color=" + color +
                    '}';
        }
    }

    public static class TokenTypeSpecs {
        private Language language;
        private boolean potentiallyLast;
        private boolean potentiallySplit;
        private boolean potentiallyComposite;
        private String pattern;
        private int maxWords;
        private int minWords;
        private CompositeSpecs compositeSpecs;

        public static class CompositeSpecs {
            private List<String> parts;
            private String splitPattern;

            public List<String> getParts() {
                return parts;
            }

            public void setParts(List<String> parts) {
                this.parts = parts;
            }

            public String getSplitPattern() {
                return splitPattern;
            }

            public void setSplitPattern(String splitPattern) {
                this.splitPattern = splitPattern;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                CompositeSpecs that = (CompositeSpecs) o;
                return getParts().equals(that.getParts()) &&
                        getSplitPattern().equals(that.getSplitPattern());
            }

            @Override
            public int hashCode() {
                return Objects.hash(getParts(), getSplitPattern());
            }

            @Override
            public String toString() {
                return "CompositeSpecs{" +
                        "parts=" + parts +
                        ", splitPattern='" + splitPattern + '\'' +
                        '}';
            }
        }

        public Language getLanguage() {
            return language;
        }

        public void setLanguage(Language language) {
            this.language = language;
        }

        public boolean isPotentiallyLast() {
            return potentiallyLast;
        }

        public void setPotentiallyLast(boolean potentiallyLast) {
            this.potentiallyLast = potentiallyLast;
        }

        public boolean isPotentiallySplit() {
            return potentiallySplit;
        }

        public void setPotentiallySplit(boolean potentiallySplit) {
            this.potentiallySplit = potentiallySplit;
        }

        public boolean isPotentiallyComposite() {
            return potentiallyComposite;
        }

        public void setPotentiallyComposite(boolean potentiallyComposite) {
            this.potentiallyComposite = potentiallyComposite;
        }

        public int getMaxWords() {
            return maxWords;
        }

        public void setMaxWords(int maxWords) {
            this.maxWords = maxWords;
        }

        public int getMinWords() {
            return minWords;
        }

        public void setMinWords(int minWords) {
            this.minWords = minWords;
        }

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

        public CompositeSpecs getCompositeSpecs() {
            return compositeSpecs;
        }

        public void setCompositeSpecs(CompositeSpecs compositeSpecs) {
            this.compositeSpecs = compositeSpecs;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TokenTypeSpecs that = (TokenTypeSpecs) o;
            return isPotentiallyLast() == that.isPotentiallyLast() &&
                    isPotentiallySplit() == that.isPotentiallySplit() &&
                    isPotentiallyComposite() == that.isPotentiallyComposite() &&
                    getLanguage() == that.getLanguage() &&
                    getMaxWords() == that.getMaxWords() &&
                    getMinWords() == that.getMinWords() &&
                    Objects.equals(getPattern(), that.getPattern()) &&
                    Objects.equals(getCompositeSpecs(), that.getCompositeSpecs());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getLanguage().ordinal(), isPotentiallyLast(), isPotentiallySplit(),
                    isPotentiallyComposite(), getPattern(), getCompositeSpecs(), getMaxWords(), getMinWords());
        }

        @Override
        public String toString() {
            return "TokenTypeSpecs{" +
                    "language=" + language +
                    ", potentiallyLast=" + potentiallyLast +
                    ", potentiallySplit=" + potentiallySplit +
                    ", potentiallyComposite=" + potentiallyComposite +
                    ", pattern='" + pattern + '\'' +
                    ", maxWords='" + maxWords + '\'' +
                    ", minWords='" + minWords + '\'' +
                    ", compositeSpecs=" + compositeSpecs +
                    '}';
        }
    }

    private VocabularyTokenType tokenType;
    private RtfSpecs rtfSpecs;
    private TokenTypeSpecs tokenTypeSpecs;

    VocabularyTokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(VocabularyTokenType tokenType) {
        this.tokenType = tokenType;
    }

    public RtfSpecs getRtfSpecs() {
        return rtfSpecs;
    }

    public void setRtfSpecs(RtfSpecs rtfSpecs) {
        this.rtfSpecs = rtfSpecs;
    }

    public TokenTypeSpecs getTokenTypeSpecs() {
        return tokenTypeSpecs;
    }

    public void setTokenTypeSpecs(TokenTypeSpecs tokenTypeSpecs) {
        this.tokenTypeSpecs = tokenTypeSpecs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VocabularyTokenSpecs that = (VocabularyTokenSpecs) o;
        return rtfSpecs.textPoints == that.rtfSpecs.textPoints &&
                rtfSpecs.isBold() == that.rtfSpecs.isBold() &&
                rtfSpecs.isItalicized() == that.rtfSpecs.italicized &&
                rtfSpecs.getColor().equals(that.rtfSpecs.getColor()) &&
                tokenTypeSpecs.language.ordinal() == that.tokenTypeSpecs.language.ordinal();
    }

    @Override
    public int hashCode() {
        return Objects.hash(rtfSpecs.getTextPoints(), rtfSpecs.isBold(), rtfSpecs.isItalicized(),
                rtfSpecs.getColor(), tokenTypeSpecs.getLanguage().ordinal());
    }

    @Override
    public String toString() {
        return "VocabularyTokenSpecs{" +
                "tokenType=" + tokenType +
                ", rtfSpecs=" + rtfSpecs +
                ", tokenTypeSpecs=" + tokenTypeSpecs +
                '}';
    }

    @Override
    public VocabularyTokenSpecs clone() {
        try {
            return (VocabularyTokenSpecs) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Can't happen
        }
    }
}