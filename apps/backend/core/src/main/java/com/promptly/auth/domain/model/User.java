package com.promptly.auth.domain.model;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

/**
 * Domain model for a User.
 * Pure domain — no framework annotations.
 */
@Data
@Builder
public class User {
    private String id;
    private String email;
    private String displayName;
    private String passwordHash;
    private String avatarUrl;
    private OrgRole orgRole;
    private UserStatus status;
    private Instant lastLoginAt;
    private Instant createdAt;
    private Instant updatedAt;
}
