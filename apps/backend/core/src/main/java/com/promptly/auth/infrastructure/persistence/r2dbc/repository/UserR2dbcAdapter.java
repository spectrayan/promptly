package com.promptly.auth.infrastructure.persistence.r2dbc.repository;

import com.promptly.auth.application.port.out.UserPersistencePort;
import com.promptly.auth.domain.model.OrgRole;
import com.promptly.auth.domain.model.User;
import com.promptly.auth.domain.model.UserStatus;
import com.promptly.auth.infrastructure.persistence.r2dbc.entity.UserR2dbcEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * R2DBC adapter implementing {@link UserPersistencePort} for PostgreSQL.
 */
@Component
@ConditionalOnProperty(name = "promptly.persistence.type", havingValue = "postgres")
@RequiredArgsConstructor
public class UserR2dbcAdapter implements UserPersistencePort {

    private final UserR2dbcRepository repository;

    @Override
    public Mono<User> save(User user) {
        return repository.save(toEntity(user)).map(this::toDomain);
    }

    @Override
    public Mono<User> findById(String id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return repository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public Mono<Long> count() {
        return repository.count();
    }

    @Override
    public Flux<User> searchByEmailOrName(String query, int limit) {
        return repository.searchByEmailOrName("%" + query + "%", limit).map(this::toDomain);
    }

    // ── Mapping ─────────────────────────────────────────────────────

    private UserR2dbcEntity toEntity(User user) {
        return UserR2dbcEntity.builder()
                .id(user.getId())
                .email(user.getEmail())
                .passwordHash(user.getPasswordHash())
                .displayName(user.getDisplayName())
                .avatarUrl(user.getAvatarUrl())
                .orgRole(user.getOrgRole() != null ? user.getOrgRole().name() : OrgRole.ORG_USER.name())
                .status(user.getStatus() != null ? user.getStatus().name() : UserStatus.ACTIVE.name())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private User toDomain(UserR2dbcEntity entity) {
        return User.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .passwordHash(entity.getPasswordHash())
                .displayName(entity.getDisplayName())
                .avatarUrl(entity.getAvatarUrl())
                .orgRole(entity.getOrgRole() != null ? OrgRole.valueOf(entity.getOrgRole()) : OrgRole.ORG_USER)
                .status(entity.getStatus() != null ? UserStatus.valueOf(entity.getStatus()) : UserStatus.ACTIVE)
                .lastLoginAt(entity.getLastLoginAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
