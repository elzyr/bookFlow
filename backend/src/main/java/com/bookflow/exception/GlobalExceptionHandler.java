package com.bookflow.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatus(ResponseStatusException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .contentType(MediaType.TEXT_PLAIN)
                .body(ex.getReason());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity
                .badRequest()
                .contentType(MediaType.TEXT_PLAIN)
                .body(ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFound(NotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.TEXT_PLAIN)
                .body(ex.getMessage());
    }

    @ExceptionHandler(InvalidOldPasswordException.class)
    public ResponseEntity<String> handleInvalidOldPassword(InvalidOldPasswordException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.TEXT_PLAIN)
                .body(ex.getMessage());
    }

    @ExceptionHandler(LoanInvalidException.class)
    public ResponseEntity<String> handleLoanAlreadyExtended(LoanInvalidException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .contentType(MediaType.TEXT_PLAIN)
                .body(ex.getMessage());
    }

    @ExceptionHandler(ExtensionNotAllowedException.class)
    public ResponseEntity<String> handleExtensionNotAllowed(ExtensionNotAllowedException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .contentType(MediaType.TEXT_PLAIN)
                .body(ex.getMessage());
    }

    @ExceptionHandler(UserRegisterException.class)
    public ResponseEntity<String> handleUserRegister(UserRegisterException ex) {
        return ResponseEntity
                .status(ex.getHttpStatus())
                .contentType(MediaType.TEXT_PLAIN)
                .body(ex.getMessage());
    }

}