package com.spectrayan.promptly.shared.domain.event;

import com.spectrayan.promptly.shared.domain.DomainEvent;

import java.time.Instant;

/**
 * Published when a new prompt is created.
 * Consumed by: search (generate embeddings), audit (log creation), notifications.
 */
public record PromptCreated(
        String aggregateId,
        String promptName,
        String projectId,
        int version,
        Instant occurredAt
) implements DomainEvent {

    public PromptCreated(String promptId, String promptName, String projectId, int version) {
        this(promptId, promptName, projectId, version, Instant.now());
    }

    /** Convenience accessor matching the old API — the aggregate ID IS the prompt ID. */
    public String promptId() { return aggregateId; }
}
