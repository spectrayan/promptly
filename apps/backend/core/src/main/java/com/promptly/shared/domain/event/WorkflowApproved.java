package com.promptly.shared.domain.event;

import com.promptly.shared.domain.DomainEvent;
import java.time.Instant;

/**
 * Published when a workflow step is approved (final approval).
 * Consumed by: prompt (mark approved), audit (log), notifications.
 */
public record WorkflowApproved(
        String aggregateId,
        String promptId,
        String promptName,
        String projectId,
        String approvedBy,
        String requesterEmail,
        Instant occurredAt
) implements DomainEvent {
    public WorkflowApproved(String workflowId, String promptId, String promptName,
                             String projectId, String approvedBy,
                             String requesterEmail) {
        this(workflowId, promptId, promptName, projectId, approvedBy,
             requesterEmail, Instant.now());
    }
}
