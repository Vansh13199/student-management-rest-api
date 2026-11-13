package com.vansh.studentmanagement.exception;

/** Thrown when a student id does not exist. Mapped to HTTP 404. */
public class StudentNotFoundException extends RuntimeException {

    public StudentNotFoundException(String message) {
        super(message);
    }
}
