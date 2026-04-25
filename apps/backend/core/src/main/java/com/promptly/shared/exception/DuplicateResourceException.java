package com.promptly.shared.exception;

/**
 * Thrown when attempting to create a resource that already exists
 * (e.g. duplicate prompt name within a project).
 */
public class DuplicateResourceException extends RuntimeException {

    private final String code;

    public DuplicateResourceException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
