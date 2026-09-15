package com.spectrayan.promptly.auth.infrastructure.persistence.r2dbc.entity;

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
 * R2DBC entity for the {@code api_keys} table.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("api_keys")
public class ApiKeyR2dbcEntity {

    @Id
    private String id;

    @Column("project_id")
    private String projectId;

    private String name;

    @Column("key_prefix")
    private String keyPrefix;

    @Column("key_hash")
    private String keyHash;

    private String roles;

    private boolean revoked;

    @Column("revoked_at")
    private Instant revokedAt;

    @Column("expires_at")
    private Instant expiresAt;

    @Column("last_used_at")
    private Instant lastUsedAt;

    @Column("created_by")
    private String createdBy;

    @CreatedDate
    @Column("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private Instant updatedAt;
}
