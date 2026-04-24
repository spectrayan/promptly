package com.promptly.prompt;

import com.promptly.shared.domain.DomainEvent;

import java.time.Instant;

/**
 * Published when a new prompt is created.
 * Consumed by: search (generate embeddings), audit (log creation), notifications.
 */
public record PromptCreated(
        String aggregateId,
        String name,
        String projectId,
        int version,
        Instant occurredAt
) implements DomainEvent {

    public PromptCreated(String promptId, String name, String projectId, int version) {
        this(promptId, name, projectId, version, Instant.now());
    }

    /** Convenience accessor matching the old API — the aggregate ID IS the prompt ID. */
    public String promptId() { return aggregateId; }
}
