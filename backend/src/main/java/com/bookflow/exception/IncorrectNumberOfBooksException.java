package com.bookflow.exception;

import org.springframework.http.HttpStatus;

public class IncorrectNumberOfBooksException extends AbstractHttpException {

    public IncorrectNumberOfBooksException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

}