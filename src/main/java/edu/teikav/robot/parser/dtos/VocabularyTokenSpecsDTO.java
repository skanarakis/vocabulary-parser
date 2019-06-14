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
    private boolean isTermPotentiallyLast;
    private boolean isTermPotentiallySplit;
    private boolean isTermPotentiallyComposite;

    public VocabularyTokenSpecsDTO() {}

    VocabularyTokenSpecsDTO(VocabularyTokenSpecs spec) {
        fontColor = spec.getRtfSpecs().getColor();
        language = spec.getTokenTypeSpecs().getLanguage();
        pattern = spec.getTokenTypeSpecs().getPattern();
        isBold = spec.getRtfSpecs().isBold();
        isItalicised = spec.getRtfSpecs().isItalicized();
        isTermPotentiallyLast = spec.getTokenTypeSpecs().isPotentiallyLast();
        isTermPotentiallySplit = spec.getTokenTypeSpecs().isPotentiallySplit();
        isTermPotentiallyComposite = spec.getTokenTypeSpecs().isPotentiallyComposite();
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

    public boolean isTermPotentiallyLast() {
        return isTermPotentiallyLast;
    }

    public void setTermPotentiallyLast(boolean termPotentiallyLast) {
        isTermPotentiallyLast = termPotentiallyLast;
    }

    public boolean isTermPotentiallySplit() {
        return isTermPotentiallySplit;
    }

    public void setTermPotentiallySplit(boolean termPotentiallySplit) {
        isTermPotentiallySplit = termPotentiallySplit;
    }

    public boolean isTermPotentiallyComposite() {
        return isTermPotentiallyComposite;
    }

    public void setTermPotentiallyComposite(boolean termPotentiallyComposite) {
        isTermPotentiallyComposite = termPotentiallyComposite;
    }
}
