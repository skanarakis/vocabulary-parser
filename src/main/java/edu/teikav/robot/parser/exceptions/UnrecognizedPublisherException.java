package edu.teikav.robot.parser.exceptions;

public class UnrecognizedPublisherException extends RuntimeException {

    private String message;

    public UnrecognizedPublisherException(String message) {
        this.message = message;
    }

    public UnrecognizedPublisherException() {}
}
