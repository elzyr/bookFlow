package com.bookflow.exception;

public class LoanInvalidException extends RuntimeException{
    public LoanInvalidException(String message) {
        super(message);
    }
}
