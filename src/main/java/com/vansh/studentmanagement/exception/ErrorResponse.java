package com.vansh.studentmanagement.exception;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Uniform JSON body returned for every error response.
 *
 * <pre>
 * {
 *   "timestamp": "2026-11-13T17:30:00",
 *   "status": 404,
 *   "error": "Not Found",
 *   "message": "Student not found with id: 99",
 *   "path": "/api/students/99",
 *   "fieldErrors": null
 * }
 * </pre>
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fieldErrors) {

    public ErrorResponse(int status, String error, String message, String path) {
        this(LocalDateTime.now(), status, error, message, path, null);
    }

    public ErrorResponse(int status, String error, String message, String path,
                         Map<String, String> fieldErrors) {
        this(LocalDateTime.now(), status, error, message, path, fieldErrors);
    }
}
