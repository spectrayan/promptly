package com.promptly.auth.infrastructure.persistence.mongo.repository;

import com.promptly.auth.application.port.out.UserPersistencePort;
import com.promptly.auth.domain.model.OrgRole;
import com.promptly.auth.domain.model.User;
import com.promptly.auth.domain.model.UserStatus;
import com.promptly.auth.infrastructure.persistence.mongo.entity.UserDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Adapter implementing the UserPersistencePort port using reactive MongoDB.
 */
@Component
@ConditionalOnProperty(name = "promptly.persistence.type", havingValue = "mongo", matchIfMissing = true)
@RequiredArgsConstructor
public class UserMongoAdapter implements UserPersistencePort {

    private final UserReactiveMongoRepository mongoRepository;

    @Override
    public Mono<User> save(User user) {
        return mongoRepository.save(toDocument(user)).map(this::toDomain);
    }

    @Override
    public Mono<User> findById(String id) {
        return mongoRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return mongoRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return mongoRepository.existsByEmail(email);
    }

    @Override
    public Mono<Long> count() {
        return mongoRepository.count();
    }

    @Override
    public Flux<User> searchByEmailOrName(String query, int limit) {
        if (query == null || query.isBlank()) {
            return mongoRepository.findAll().take(limit).map(this::toDomain);
        }
        return mongoRepository
                .findByEmailContainingIgnoreCaseOrDisplayNameContainingIgnoreCase(query, query)
                .take(limit)
                .map(this::toDomain);
    }

    // ── Mapping ──

    private User toDomain(UserDocument doc) {
        return User.builder()
                .id(doc.getId())
                .email(doc.getEmail())
                .displayName(doc.getDisplayName())
                .passwordHash(doc.getPasswordHash())
                .avatarUrl(doc.getAvatarUrl())
                .orgRole(OrgRole.valueOf(doc.getOrgRole()))
                .status(UserStatus.valueOf(doc.getStatus()))
                .lastLoginAt(doc.getLastLoginAt())
                .createdAt(doc.getCreatedAt())
                .updatedAt(doc.getUpdatedAt())
                .build();
    }

    private UserDocument toDocument(User user) {
        return UserDocument.builder()
                .id(user.getId())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .passwordHash(user.getPasswordHash())
                .avatarUrl(user.getAvatarUrl())
                .orgRole(user.getOrgRole().name())
                .status(user.getStatus().name())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
