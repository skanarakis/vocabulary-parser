package edu.teikav.robot.parser.exceptions;

public class UnknownGrammarException extends RuntimeException {

    private String message;

    public UnknownGrammarException(String message) {
        this.message = message;
    }

    public UnknownGrammarException() {}
}
