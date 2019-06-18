package edu.teikav.robot.parser.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR, reason="Invalid Graph for Publisher")
public class InvalidGraphException extends RuntimeException {

    public InvalidGraphException(String message) {
        super(message);
    }
}
