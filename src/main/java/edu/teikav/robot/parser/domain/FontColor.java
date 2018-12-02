package edu.teikav.robot.parser.domain;

import java.util.Objects;

public class FontColor {

    public static final FontColor BLACK = new FontColor(0, 0, 0);

    private int red;
    private int green;
    private int blue;

    public FontColor() {}

    public FontColor(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public FontColor(FontColor source) {
        this.red = source.red;
        this.green = source.green;
        this.blue = source.blue;
    }

    public int getRed() {
        return red;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public int getGreen() {
        return green;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public int getBlue() {
        return blue;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }

    @Override
    public String toString() {
        return "FontColor{" + "red=" + red +
                ", green=" + green +
                ", blue=" + blue +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FontColor fontColor = (FontColor) o;
        return red == fontColor.red &&
                green == fontColor.green &&
                blue == fontColor.blue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(red, green, blue);
    }
}
