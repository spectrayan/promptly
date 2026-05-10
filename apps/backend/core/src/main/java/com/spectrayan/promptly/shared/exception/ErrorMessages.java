package com.spectrayan.promptly.shared.exception;

/**
 * Centralized error message constants for consistent user-facing error responses.
 * <p>
 * Patterns:
 * <ul>
 *   <li>Use {@code %s} placeholders for dynamic values (resource names, IDs, etc.)</li>
 *   <li>Messages should be clear, actionable, and user-friendly</li>
 * </ul>
 */
public final class ErrorMessages {

    private ErrorMessages() {} // utility class

    // ═══════════════════════════════════════════════════════════════════
    // Duplicate / Conflict
    // ═══════════════════════════════════════════════════════════════════

    public static final String DUPLICATE_PROMPT_NAME =
            "A prompt named '%s' already exists in this project";

    public static final String DUPLICATE_PROJECT_NAME =
            "A project named '%s' already exists";

    public static final String DUPLICATE_PROJECT_MEMBER =
            "User is already a member of this project";

    public static final String DUPLICATE_RESOURCE_GENERIC =
            "A resource with the same unique key already exists";

    // ═══════════════════════════════════════════════════════════════════
    // Not Found
    // ═══════════════════════════════════════════════════════════════════

    public static final String RESOURCE_NOT_FOUND =
            "%s not found: %s";

    // ═══════════════════════════════════════════════════════════════════
    // Auth
    // ═══════════════════════════════════════════════════════════════════

    public static final String INVALID_CREDENTIALS =
            "Invalid email or password";

    // ═══════════════════════════════════════════════════════════════════
    // Business Rules
    // ═══════════════════════════════════════════════════════════════════

    public static final String PROMPT_NOT_EDITABLE =
            "Prompt cannot be modified in its current state";

    public static final String PROMPT_NOT_SUBMITTABLE =
            "Prompt cannot be submitted for review in its current state";

    // ═══════════════════════════════════════════════════════════════════
    // Generic
    // ═══════════════════════════════════════════════════════════════════

    public static final String UNEXPECTED_ERROR =
            "An unexpected error occurred";
}
