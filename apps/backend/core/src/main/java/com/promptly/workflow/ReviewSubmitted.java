package com.promptly.workflow;

import com.promptly.shared.domain.DomainEvent;
import java.time.Instant;

/**
 * Published when a workflow review is submitted.
 * Consumed by: audit (log the review submission).
 */
public record ReviewSubmitted(
        String aggregateId,
        String promptId,
        int promptVersion,
        String requestedBy,
        Instant occurredAt
) implements DomainEvent {
    public ReviewSubmitted(String workflowId, String promptId, int promptVersion, String requestedBy) {
        this(workflowId, promptId, promptVersion, requestedBy, Instant.now());
    }
}
