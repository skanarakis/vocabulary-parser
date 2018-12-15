package edu.teikav.robot.parser.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VocabularyToken implements Cloneable {

    private Languages language;
    private int textPoints;
    private boolean italicized;
    private boolean bold;
    private FontColor color = FontColor.BLACK;
    private String tokenString;
    private VocabularyTokenType tokenType;

    public Languages getLanguage() {
        return language;
    }

    public void setLanguage(Languages language) {
        this.language = language;
    }

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

    public String getTokenString() {
        return tokenString;
    }

    public void setTokenString(String tokenString) {
        this.tokenString = tokenString;
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
        return language == that.language &&
                textPoints == that.textPoints &&
                italicized == that.italicized &&
                bold == that.bold &&
                color == that.color;
    }

    @Override
    public int hashCode() {
        // We need to have the same Hash-Code among different runs, so we use the ENUM ordinal instead of ENUM itself
        return Objects.hash(language.ordinal(), textPoints, italicized, bold, color);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nVocabularyToken{");
        if (tokenString != null) {
            sb.append("\n\tToken = ").append(tokenString);
        }
        if (tokenType != null) {
            sb.append("\n\tTokenType = ").append(tokenType);
        }
        sb.append("\n\tlanguage=").append(language);
        sb.append("\n\ttextPoints=").append(textPoints);
        if (italicized) {
            sb.append("\n\titalicized");
        }
        if (bold) {
            sb.append("\n\tbold");
        }
        sb.append("\n\tcolor=").append(color);

        return sb.toString();
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