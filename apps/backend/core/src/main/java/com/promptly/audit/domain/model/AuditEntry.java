package com.promptly.audit.domain.model;

import com.promptly.shared.domain.AggregateRoot;
import lombok.experimental.SuperBuilder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Map;

/**
 * Immutable audit log entry.
 * Append-only — no updates or deletes allowed.
 */
@Getter
@Setter
@SuperBuilder
public class AuditEntry extends AggregateRoot {

    private String action; // prompt.created, prompt.updated, workflow.approved, scan.completed, etc.
    private String resourceType; // prompt, workflow, scan
    private String resourceId;
    private Integer resourceVersion;
    private String actorUserId;
    private String actorEmail;
    private String actorRole;
    private Map<String, Object> details;
    private Instant timestamp;

}
