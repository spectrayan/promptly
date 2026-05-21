package com.spectrayan.promptly.audit.infrastructure.persistence.mongo.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

/**
 * MongoDB document for audit log entries.
 * Write-only — no update/delete operations.
 */
@Data
@Builder
@Document(collection = "audit_logs")
@CompoundIndexes({
        @CompoundIndex(name = "idx_resource_timestamp", def = "{'resourceId': 1, 'timestamp': -1}"),
        @CompoundIndex(name = "idx_actor_timestamp", def = "{'actorUserId': 1, 'timestamp': -1}"),
        @CompoundIndex(name = "idx_action_timestamp", def = "{'action': 1, 'timestamp': -1}")
})
public class AuditDocument {

    @Id
    private String id;

    private String projectId;
    private String action;
    private String resourceType;
    private String resourceId;
    private Integer resourceVersion;
    private String actorUserId;
    private String actorEmail;
    private String actorRole;
    private Map<String, Object> details;
    private Instant timestamp;

    @CreatedDate
    private Instant createdAt;

}
