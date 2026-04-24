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
    private String action; // pending | approved | rejected
    private String comment;
    private Instant actedAt;

    public boolean isPending() {
        return "pending".equalsIgnoreCase(action);
    }

    public void approve(String comment) {
        this.action = "approved";
        this.comment = comment;
        this.actedAt = Instant.now();
    }

    public void reject(String comment) {
        this.action = "rejected";
        this.comment = comment;
        this.actedAt = Instant.now();
    }

}
