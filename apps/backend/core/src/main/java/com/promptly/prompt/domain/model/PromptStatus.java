package com.promptly.prompt.domain.model;

/**
 * Lifecycle status of a prompt, independent of deployment environment.
 * <p>
 * This tracks whether the prompt is actively being modified, under review,
 * or has been approved/deployed. The environment field ({@code activeEnvironment})
 * tracks WHERE it is deployed; this status tracks its review state.
 */
public enum PromptStatus {

    /** Default — prompt is being actively developed */
    DRAFT,

    /** A workflow has been submitted and is pending approval */
    IN_REVIEW,

    /** The latest workflow was approved */
    APPROVED
}
