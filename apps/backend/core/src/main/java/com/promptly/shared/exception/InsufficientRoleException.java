package com.promptly.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a user lacks the required project role for an operation.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class InsufficientRoleException extends RuntimeException {

    public InsufficientRoleException(String projectId, String action) {
        super("You do not have permission to " + action + " in project: " + projectId);
    }
}
