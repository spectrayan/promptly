package com.promptly.shared.domain.event;

import com.promptly.shared.domain.DomainEvent;
import java.time.Instant;

/**
 * Published when a workflow step is rejected.
 * Consumed by: audit (log the rejection), notifications (email requester).
 */
public record WorkflowRejected(
        String aggregateId,
        String promptId,
        String promptName,
        String projectId,
        String rejectedBy,
        String reason,
        String requesterEmail,
        Instant occurredAt
) implements DomainEvent {
    public WorkflowRejected(String workflowId, String promptId, String promptName,
                             String projectId, String rejectedBy, String reason,
                             String requesterEmail) {
        this(workflowId, promptId, promptName, projectId, rejectedBy, reason,
             requesterEmail, Instant.now());
    }
}
