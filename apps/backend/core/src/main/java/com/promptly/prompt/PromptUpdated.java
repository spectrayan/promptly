package com.promptly.prompt;

import com.promptly.shared.domain.DomainEvent;

import java.time.Instant;

/**
 * Published when an existing prompt version is updated.
 * Consumed by: scanner (re-scan), search (update embeddings), audit (log change), notifications.
 */
public record PromptUpdated(
        String aggregateId,
        String projectId,
        int version,
        Instant occurredAt
) implements DomainEvent {

    public PromptUpdated(String promptId, String projectId, int version) {
        this(promptId, projectId, version, Instant.now());
    }

    public String promptId() { return aggregateId; }
}
