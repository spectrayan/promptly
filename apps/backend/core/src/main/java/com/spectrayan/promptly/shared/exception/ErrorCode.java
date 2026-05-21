package com.spectrayan.promptly.shared.exception;

/**
 * Machine-readable error codes sent in the {@code code} extension property
 * of RFC 9457 Problem Details responses.
 * <p>
 * The frontend uses these codes to look up its own user-friendly, localisable
 * error messages — the backend's {@code detail} field is a fallback only.
 * <p>
 * Convention: {@code DOMAIN_VERB_REASON} in UPPER_SNAKE_CASE.
 */
public final class ErrorCode {

    private ErrorCode() {} // utility class

    // ═══════════════════════════════════════════════════════════════════
    // Prompt
    // ═══════════════════════════════════════════════════════════════════

    public static final String PROMPT_DUPLICATE_NAME     = "PROMPT_DUPLICATE_NAME";
    public static final String PROMPT_NOT_FOUND          = "PROMPT_NOT_FOUND";
    public static final String PROMPT_NOT_EDITABLE       = "PROMPT_NOT_EDITABLE";
    public static final String PROMPT_NOT_SUBMITTABLE    = "PROMPT_NOT_SUBMITTABLE";

    // ═══════════════════════════════════════════════════════════════════
    // Project
    // ═══════════════════════════════════════════════════════════════════

    public static final String PROJECT_DUPLICATE_NAME    = "PROJECT_DUPLICATE_NAME";
    public static final String PROJECT_NOT_FOUND         = "PROJECT_NOT_FOUND";
    public static final String PROJECT_MEMBER_EXISTS     = "PROJECT_MEMBER_EXISTS";
    public static final String PROJECT_MEMBER_NOT_FOUND  = "PROJECT_MEMBER_NOT_FOUND";

    // ═══════════════════════════════════════════════════════════════════
    // Workflow
    // ═══════════════════════════════════════════════════════════════════

    public static final String WORKFLOW_NOT_FOUND        = "WORKFLOW_NOT_FOUND";
    public static final String WORKFLOW_ALREADY_COMPLETE  = "WORKFLOW_ALREADY_COMPLETE";

    // ═══════════════════════════════════════════════════════════════════
    // Auth
    // ═══════════════════════════════════════════════════════════════════

    public static final String AUTH_INVALID_CREDENTIALS  = "AUTH_INVALID_CREDENTIALS";
    public static final String AUTH_USER_EXISTS           = "AUTH_USER_EXISTS";

    // ═══════════════════════════════════════════════════════════════════
    // Generic
    // ═══════════════════════════════════════════════════════════════════

    public static final String DUPLICATE_RESOURCE        = "DUPLICATE_RESOURCE";
    public static final String RESOURCE_NOT_FOUND        = "RESOURCE_NOT_FOUND";
    public static final String BAD_REQUEST               = "BAD_REQUEST";
    public static final String BUSINESS_RULE_VIOLATION   = "BUSINESS_RULE_VIOLATION";
    public static final String INTERNAL_ERROR            = "INTERNAL_ERROR";
}
