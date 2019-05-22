package edu.teikav.robot.parser.domain;

public enum Language {

    ENGLISH,
    GREEK;

    public static Language languageFromID(int languageID) {
        switch (languageID) {
            case 9:
            case 1033:
                return ENGLISH;
            case 1032:
                return GREEK;
            default:
                return null;
        }
    }
}
