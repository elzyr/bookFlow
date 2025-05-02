package com.bookflow.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class AbstractHttpException extends RuntimeException {
    private final HttpStatus httpStatus;

    public AbstractHttpException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

}
