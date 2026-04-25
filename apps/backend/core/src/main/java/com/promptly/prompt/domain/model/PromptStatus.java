package com.promptly.prompt.domain.model;

/**
 * Lifecycle status of a prompt.
 * <p>
 * Tracks whether the prompt is actively being modified, under review,
 * or has been approved.
 */
public enum PromptStatus {

    /** Default — prompt is being actively developed */
    DRAFT,

    /** A workflow has been submitted and is pending approval */
    IN_REVIEW,

    /** The latest workflow was approved */
    APPROVED
}
