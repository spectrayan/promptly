package com.spectrayan.promptly.auth.domain.model;

import com.spectrayan.promptly.shared.domain.AggregateRoot;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.List;

/**
 * Domain entity representing an API key scoped to a project.
 * Used for machine-to-machine Runtime Delivery prompt access without JWTs.
 * Plaintext keys are NEVER stored; only a secure SHA-256 hash is persisted at rest.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class ApiKey extends AggregateRoot {

    private String projectId;
    private String name;
    private String keyPrefix;
    private String keyHash;
    private List<String> roles;
    private boolean revoked;
    private Instant revokedAt;
    private Instant expiresAt;
    private Instant lastUsedAt;
    private String createdBy;

    public boolean isValid() {
        if (revoked) {
            return false;
        }
        return expiresAt == null || expiresAt.isAfter(Instant.now());
    }

    public void revoke() {
        this.revoked = true;
        this.revokedAt = Instant.now();
        setUpdatedAt(Instant.now());
    }

    public void recordUsage() {
        this.lastUsedAt = Instant.now();
        setUpdatedAt(Instant.now());
    }
}
