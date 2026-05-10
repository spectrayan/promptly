package com.spectrayan.promptly.shared.exception;

/**
 * Thrown when authentication fails — wrong credentials, inactive account, etc.
 * <p>
 * Mapped to {@code 401 Unauthorized} by the {@link GlobalExceptionHandler}.
 */
public class AuthenticationFailedException extends RuntimeException {

    private final String code;

    public AuthenticationFailedException(String message) {
        this(message, ErrorCode.AUTH_INVALID_CREDENTIALS);
    }

    public AuthenticationFailedException(String message, String code) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
