package com.promptly.auth.infrastructure.persistence.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * MongoDB document for the User entity.
 * Only used in LOCAL auth mode. In OIDC mode, user identity comes from the IdP.
 */
@Data
@Builder
@Document(collection = "users")
public class UserDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private String displayName;
    private String passwordHash;
    private String avatarUrl;
    private String orgRole;
    private String status;
    private Instant lastLoginAt;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
