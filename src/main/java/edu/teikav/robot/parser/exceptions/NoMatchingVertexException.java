package edu.teikav.robot.parser.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR, reason="Internal Vocabulary Recognizer processing error")
public class NoMatchingVertexException extends RuntimeException {

    public NoMatchingVertexException(String message) {
        super(message);
    }
}
