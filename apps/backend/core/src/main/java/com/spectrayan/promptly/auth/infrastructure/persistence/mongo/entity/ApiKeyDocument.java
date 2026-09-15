package com.spectrayan.promptly.auth.infrastructure.persistence.mongo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

/**
 * MongoDB document for ApiKey entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "api_keys")
public class ApiKeyDocument {

    @Id
    private String id;

    @Indexed
    private String projectId;

    private String name;
    private String keyPrefix;

    @Indexed(unique = true)
    private String keyHash;

    private List<String> roles;
    private boolean revoked;
    private Instant revokedAt;
    private Instant expiresAt;
    private Instant lastUsedAt;
    private String createdBy;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
