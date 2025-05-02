package com.bookflow.exception;

import org.springframework.http.HttpStatus;

public class InvalidOldPasswordException extends AbstractHttpException {
    public InvalidOldPasswordException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
