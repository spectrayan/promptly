package com.spectrayan.promptly.shared.domain.event;

import com.spectrayan.promptly.shared.domain.DomainEvent;

import java.time.Instant;

/**
 * Domain event published when a prompt is delivered via the Runtime Delivery API.
 * Captured by {@code AuditAspect} to record machine-to-machine prompt access.
 */
public record PromptDelivered(
        String aggregateId,
        String projectId,
        String promptName,
        String usecase,
        String agent,
        String authType,
        String actorId,
        Instant occurredAt
) implements DomainEvent {

    public PromptDelivered(String aggregateId, String projectId, String promptName,
                           String usecase, String agent, String authType, String actorId) {
        this(aggregateId, projectId, promptName, usecase, agent, authType, actorId, Instant.now());
    }
}
