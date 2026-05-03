package com.promptly.prompt.infrastructure.persistence.r2dbc.entity;

import io.r2dbc.postgresql.codec.Json;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

/**
 * R2DBC entity for the {@code prompts} table.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("prompts")
public class PromptR2dbcEntity {

    @Id
    private String id;

    private String name;
    private String description;

    @Column("project_id")
    private String projectId;

    @Column("content_format")
    private String contentFormat;

    /** JSON array of tags — stored as JSONB in PostgreSQL, uses native Json codec. */
    private Json tags;

    @Column("metadata_model")
    private String metadataModel;

    @Column("metadata_temperature")
    private Double metadataTemperature;

    @Column("metadata_max_tokens")
    private Integer metadataMaxTokens;

    @Column("metadata_system_context")
    private String metadataSystemContext;

    @Column("current_version")
    private int currentVersion;

    private String status;

    @Version
    private Long version;

    @CreatedDate
    @Column("created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private Instant updatedAt;

    @Column("created_by")
    private String createdBy;

    @Column("updated_by")
    private String updatedBy;
}
