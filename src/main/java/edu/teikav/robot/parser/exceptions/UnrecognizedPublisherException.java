package edu.teikav.robot.parser.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR, reason="Publisher Format cannot be identified")
public class UnrecognizedPublisherException extends RuntimeException {

    public UnrecognizedPublisherException() {}
}
