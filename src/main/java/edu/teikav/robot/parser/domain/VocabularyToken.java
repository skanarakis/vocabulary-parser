package edu.teikav.robot.parser.domain;

import java.util.Objects;

public class VocabularyToken implements Cloneable {

    private int textPoints;
    private boolean italicized;
    private boolean bold;
    private FontColor color;
    private String value;
    private Language language;
    private VocabularyTokenType tokenType;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public VocabularyTokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(VocabularyTokenType tokenType) {
        this.tokenType = tokenType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VocabularyToken that = (VocabularyToken) o;
        return getTextPoints() == that.getTextPoints() &&
                isItalicized() == that.isItalicized() &&
                isBold() == that.isBold() &&
                getColor().equals(that.getColor()) &&
                getLanguage() == that.getLanguage();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTextPoints(), isBold(), isItalicized(), getColor(), getLanguage().ordinal());
    }

    @Override
    public String toString() {
        return "VocabularyToken{" +
                "textPoints=" + textPoints +
                ", italicized=" + italicized +
                ", bold=" + bold +
                ", color=" + color +
                ", value='" + value + '\'' +
                ", language=" + language +
                ", tokenType=" + tokenType +
                '}';
    }

    @Override
    public VocabularyToken clone() {
        try {
            return (VocabularyToken) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Can't happen
        }
    }
}


