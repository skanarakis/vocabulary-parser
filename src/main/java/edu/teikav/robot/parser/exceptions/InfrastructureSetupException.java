package edu.teikav.robot.parser.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR, reason="Infrastructure setup exception")
public class InfrastructureSetupException extends RuntimeException {

    public InfrastructureSetupException(Throwable cause) {
        super(cause);
    }
}
