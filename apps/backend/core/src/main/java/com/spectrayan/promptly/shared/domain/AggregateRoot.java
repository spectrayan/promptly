package com.spectrayan.promptly.shared.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class for aggregate roots.
 * Provides common fields for identity, auditing, and optimistic concurrency.
 * <p>
 * Domain events are accumulated via {@link #registerEvent(DomainEvent)} and
 * can be harvested with {@link #domainEvents()} / {@link #clearDomainEvents()}.
 * The persistence layer (or an application service) is responsible for
 * publishing these events after the aggregate is successfully persisted.
 * <p>
 * This is a pure POJO — NO framework annotations (no @Document, no @Id).
 * MongoDB-specific annotations live on the Document classes in the infrastructure layer.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public abstract class AggregateRoot {

    private String id;
    private Long version;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    // ── Domain Events ────────────────────────────────────────────

    /**
     * Transient list of domain events raised during the current operation.
     * Not persisted — must be drained and published before the aggregate is detached.
     */
    @lombok.Builder.Default
    private final transient List<DomainEvent> domainEvents = new ArrayList<>();

    /**
     * Register a domain event to be published after this aggregate is persisted.
     *
     * @param event the domain event to register
     */
    protected void registerEvent(DomainEvent event) {
        domainEvents.add(event);
    }

    /**
     * Return an unmodifiable view of the accumulated domain events.
     */
    public List<DomainEvent> domainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    /**
     * Clear all accumulated domain events. Should be called after events are published.
     *
     * @return the events that were cleared (so the caller can publish them)
     */
    public List<DomainEvent> clearDomainEvents() {
        List<DomainEvent> events = new ArrayList<>(domainEvents);
        domainEvents.clear();
        return events;
    }
}
