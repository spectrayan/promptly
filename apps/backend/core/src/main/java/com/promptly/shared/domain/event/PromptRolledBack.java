package com.promptly.shared.domain.event;

import com.promptly.shared.domain.DomainEvent;

import java.time.Instant;

/**
 * Published when a prompt is rolled back to a previous version.
 * Consumed by: scanner (re-scan rolled-back version), audit (log rollback).
 */
public record PromptRolledBack(
        String aggregateId,
        int fromVersion,
        int toVersion,
        Instant occurredAt
) implements DomainEvent {

    public PromptRolledBack(String promptId, int fromVersion, int toVersion) {
        this(promptId, fromVersion, toVersion, Instant.now());
    }

}
