package com.spectrayan.promptly.llmconfig.infrastructure.persistence.r2dbc.entity;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

/**
 * R2DBC entity for the {@code llm_configs} table.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("llm_configs")
public class LlmConfigR2dbcEntity {

    @Id
    private String id;

    @Column("project_id")
    private String projectId;

    private String feature;
    private String provider;
    private String model;

    @Column("encrypted_api_key")
    private String encryptedApiKey;

    @Column("base_url")
    private String baseUrl;

    private Double temperature;

    @Column("max_tokens")
    private Integer maxTokens;

    @LastModifiedDate
    @Column("updated_at")
    private Instant updatedAt;

    @Column("updated_by")
    private String updatedBy;
}
