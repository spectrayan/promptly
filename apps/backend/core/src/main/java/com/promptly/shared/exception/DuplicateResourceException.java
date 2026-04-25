package com.promptly.shared.exception;

/**
 * Thrown when attempting to create a resource that already exists
 * (e.g. duplicate prompt name within a project).
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
