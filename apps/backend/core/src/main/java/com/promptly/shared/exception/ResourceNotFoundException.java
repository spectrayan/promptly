package com.promptly.shared.exception;

/**
 * Thrown when a requested resource does not exist.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceType, String id) {
        super(String.format("%s with id '%s' not found", resourceType, id));
    }

}
