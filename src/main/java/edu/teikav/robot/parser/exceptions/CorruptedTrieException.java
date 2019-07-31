package edu.teikav.robot.parser.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR, reason="Corrupted Inventory Structure")
public class CorruptedTrieException extends RuntimeException {

    public CorruptedTrieException(String message) {
        super(message);
    }
}
