package com.promptly.audit.infrastructure.persistence.r2dbc.entity;

import com.promptly.shared.config.r2dbc.converter.JsonColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

/**
 * R2DBC entity for the {@code audit_logs} table.
 * <p>
 * The {@code id} field is left {@code null} for new entities so that
 * Spring Data R2DBC performs an INSERT (isNew = id == null) and the
 * database generates the UUID via {@code gen_random_uuid()} or equivalent.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("audit_logs")
public class AuditLogR2dbcEntity {

    @Id
    private String id;

    @Column("project_id")
    private String projectId;

    private String action;

    @Column("resource_type")
    private String resourceType;

    @Column("resource_id")
    private String resourceId;

    @Column("resource_version")
    private Integer resourceVersion;

    @Column("actor_user_id")
    private String actorUserId;

    @Column("actor_email")
    private String actorEmail;

    @Column("actor_role")
    private String actorRole;

    /** JSON details column — JSONB on PostgreSQL, JSON on H2. */
    private JsonColumn details;

    private Instant timestamp;

    @CreatedDate
    @Column("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private Instant updatedAt;
}
