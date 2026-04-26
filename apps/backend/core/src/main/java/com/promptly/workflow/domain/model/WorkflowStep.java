package com.promptly.workflow.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Represents a single step in a workflow approval chain.
 */
@Getter
@Setter
@Builder
public class WorkflowStep {

    private int step;
    private String role;
    private String assignedTo;
    private String action; // PENDING | APPROVED | REJECTED
    private String comment;
    private Instant actedAt;

    public boolean isPending() {
        return "PENDING".equalsIgnoreCase(action);
    }

    public void approve(String comment) {
        this.action = "APPROVED";
        this.comment = comment;
        this.actedAt = Instant.now();
    }

    public void reject(String comment) {
        this.action = "REJECTED";
        this.comment = comment;
        this.actedAt = Instant.now();
    }

}
