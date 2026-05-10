package com.spectrayan.promptly.shared.domain;

import java.time.Instant;

/**
 * Base record for all domain events published across modules.
 * Events are the ONLY mechanism for inter-module communication in Spring Modulith.
 */
public interface DomainEvent {

    /**
     * @return Unique identifier of the aggregate that produced this event
     */
    String aggregateId();

    /**
     * @return Timestamp when the event occurred
     */
    Instant occurredAt();

}
