package com.de.exceptions;

public class DuplicatedMessageException extends RuntimeException {
    public DuplicatedMessageException(String message){
        super(message);
    }
}
