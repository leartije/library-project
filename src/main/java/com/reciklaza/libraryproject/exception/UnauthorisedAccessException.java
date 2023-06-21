package com.reciklaza.libraryproject.exception;

public class UnauthorisedAccessException extends RuntimeException {

    public UnauthorisedAccessException(String message) {
        super(message);
    }
}
