package com.bookflow.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends AbstractHttpException {

    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

}
