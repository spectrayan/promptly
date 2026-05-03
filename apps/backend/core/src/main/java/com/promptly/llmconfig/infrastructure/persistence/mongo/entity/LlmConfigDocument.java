package com.promptly.llmconfig.infrastructure.persistence.mongo.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * MongoDB document for LLM configuration.
 * Unique per (projectId, feature) combination.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("llm_configs")
@CompoundIndex(name = "idx_project_feature", def = "{'projectId': 1, 'feature': 1}", unique = true)
public class LlmConfigDocument {

    @Id
    private String id;

    @Version
    private Long version;

    /** Project ID. Null means platform-global. */
    private String projectId;

    /** Feature scope: global, scanner, improver, embedding. */
    private String feature;

    private String provider;
    private String model;
    private Double temperature;
    private Integer maxTokens;
    private String baseUrl;

    /** Encrypted API key — stored as "enc:aes256:<base64>". Never null-checked against env. */
    private String encryptedApiKey;

    private Instant updatedAt;
    private String updatedBy;
}
