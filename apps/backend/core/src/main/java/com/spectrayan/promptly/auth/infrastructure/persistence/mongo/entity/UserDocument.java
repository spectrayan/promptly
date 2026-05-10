package com.spectrayan.promptly.auth.infrastructure.persistence.mongo.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotBlank
    @Email
    @Size(max = 254)
    @Indexed(unique = true)
    private String email;

    @NotBlank
    @Size(max = 200)
    private String displayName;

    @NotBlank
    private String passwordHash;
    private String avatarUrl;

    @NotNull
    private String orgRole;

    @NotNull
    private String status;
    private Instant lastLoginAt;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
