package com.spectrayan.promptly.shared.exception;

/**
 * Thrown when a requested resource does not exist.
 */
public class ResourceNotFoundException extends RuntimeException {

    private final String code;

    public ResourceNotFoundException(String resourceType, String id) {
        super(String.format("%s with id '%s' not found", resourceType, id));
        this.code = ErrorCode.RESOURCE_NOT_FOUND;
    }

    public ResourceNotFoundException(String code, String resourceType, String id) {
        super(String.format("%s with id '%s' not found", resourceType, id));
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
