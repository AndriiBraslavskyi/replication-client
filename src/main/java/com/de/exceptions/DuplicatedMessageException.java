package com.de.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class DuplicatedMessageException extends RuntimeException {
    public DuplicatedMessageException(String message){
        super(message);
    }
}
