package com.promptly.prompt.domain.model;

/**
 * Lifecycle status of a prompt.
 * <p>
 * Tracks whether the prompt is actively being modified, under review,
 * approved, rejected, or deprecated.
 */
public enum PromptStatus {

    /** Default — prompt is being actively developed */
    DRAFT,

    /** A workflow has been submitted and is pending approval */
    IN_REVIEW,

    /** The latest workflow was approved */
    APPROVED,

    /** The latest workflow was rejected */
    REJECTED,

    /** Prompt is no longer recommended for use */
    DEPRECATED
}
