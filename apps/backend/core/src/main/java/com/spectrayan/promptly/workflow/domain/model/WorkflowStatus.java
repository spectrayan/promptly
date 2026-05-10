package com.spectrayan.promptly.workflow.domain.model;

/**
 * Status of a workflow.
 */
public enum WorkflowStatus {
    PENDING,
    IN_REVIEW,
    APPROVED,
    REJECTED,
    CANCELLED
}
