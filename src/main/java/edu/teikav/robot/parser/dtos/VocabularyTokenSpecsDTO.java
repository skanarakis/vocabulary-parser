package edu.teikav.robot.parser.dtos;

import edu.teikav.robot.parser.domain.FontColor;
import edu.teikav.robot.parser.domain.Language;
import edu.teikav.robot.parser.domain.VocabularyTokenSpecs;

public class VocabularyTokenSpecsDTO {

    private FontColor fontColor;
    private Language language;
    private String pattern;
    private boolean isBold;
    private boolean isItalicised;

    public VocabularyTokenSpecsDTO() {}

    VocabularyTokenSpecsDTO(VocabularyTokenSpecs spec) {
        fontColor = spec.getRtfSpecs().getColor();
        language = spec.getTokenTypeSpecs().getLanguage();
        pattern = spec.getTokenTypeSpecs().getPattern();
        isBold = spec.getRtfSpecs().isBold();
        isItalicised = spec.getRtfSpecs().isItalicized();
    }

    public FontColor getFontColor() {
        return fontColor;
    }

    public void setFontColor(FontColor fontColor) {
        this.fontColor = fontColor;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public boolean isBold() {
        return isBold;
    }

    public void setBold(boolean bold) {
        isBold = bold;
    }

    public boolean isItalicised() {
        return isItalicised;
    }

    public void setItalicised(boolean italicised) {
        isItalicised = italicised;
    }
}
