package edu.teikav.robot.parser.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR, reason="Vocabulary could not be parsed")
public class VocabularyParsingException extends RuntimeException {

    public VocabularyParsingException(Throwable cause) {
        super(cause);
    }
}
