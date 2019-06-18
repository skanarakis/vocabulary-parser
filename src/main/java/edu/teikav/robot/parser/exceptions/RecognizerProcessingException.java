package edu.teikav.robot.parser.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR, reason="Vocabulary could not be properly recognized")
public class RecognizerProcessingException extends RuntimeException {

    public RecognizerProcessingException(String message) {
        super(message);
    }
}
