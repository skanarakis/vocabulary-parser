package edu.teikav.robot.parser.domain;

public enum Languages {

    ENGLISH,
    GREEK;

    public static Languages languageFromID(int languageID) {
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
