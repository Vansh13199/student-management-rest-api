package com.vansh.studentmanagement.exception;

/** Thrown when a student email is already registered. Mapped to HTTP 409. */
public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String message) {
        super(message);
    }
}
