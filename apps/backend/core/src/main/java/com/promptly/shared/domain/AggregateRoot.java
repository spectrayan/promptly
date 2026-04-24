package com.promptly.shared.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

/**
 * Base class for aggregate roots.
 * Provides common fields for identity, auditing, and optimistic concurrency.
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

}
