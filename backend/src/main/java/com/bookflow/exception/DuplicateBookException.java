package com.bookflow.exception;

public class DuplicateBookException extends RuntimeException {
    public DuplicateBookException(String msg) { super(msg); }
}