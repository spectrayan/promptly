package com.spectrayan.promptly.auth.infrastructure.persistence.r2dbc.repository;

import com.spectrayan.promptly.auth.application.port.out.ApiKeyPersistencePort;
import com.spectrayan.promptly.auth.domain.model.ApiKey;
import com.spectrayan.promptly.auth.infrastructure.persistence.r2dbc.entity.ApiKeyR2dbcEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
@ConditionalOnProperty(name = "promptly.persistence.type", havingValue = "sql")
@RequiredArgsConstructor
public class ApiKeyR2dbcAdapter implements ApiKeyPersistencePort {

    private final ApiKeyReactiveR2dbcRepository repository;

    @Override
    public Mono<ApiKey> save(ApiKey apiKey) {
        return repository.save(toEntity(apiKey)).map(this::toDomain);
    }

    @Override
    public Mono<ApiKey> findById(String id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Mono<ApiKey> findByKeyHash(String keyHash) {
        return repository.findByKeyHash(keyHash).map(this::toDomain);
    }

    @Override
    public Flux<ApiKey> findByProjectId(String projectId) {
        return repository.findByProjectId(projectId).map(this::toDomain);
    }

    @Override
    public Mono<Boolean> existsByIdAndProjectId(String id, String projectId) {
        return repository.existsByIdAndProjectId(id, projectId);
    }

    // ── Mapping ─────────────────────────────────────────────────────

    private ApiKeyR2dbcEntity toEntity(ApiKey key) {
        String rolesStr = (key.getRoles() != null && !key.getRoles().isEmpty())
                ? String.join(",", key.getRoles())
                : "ROLE_API_KEY";

        return ApiKeyR2dbcEntity.builder()
                .id(key.getId())
                .projectId(key.getProjectId())
                .name(key.getName())
                .keyPrefix(key.getKeyPrefix())
                .keyHash(key.getKeyHash())
                .roles(rolesStr)
                .revoked(key.isRevoked())
                .revokedAt(key.getRevokedAt())
                .expiresAt(key.getExpiresAt())
                .lastUsedAt(key.getLastUsedAt())
                .createdBy(key.getCreatedBy())
                .createdAt(key.getCreatedAt())
                .updatedAt(key.getUpdatedAt())
                .build();
    }

    private ApiKey toDomain(ApiKeyR2dbcEntity entity) {
        List<String> roles = (entity.getRoles() != null && !entity.getRoles().isBlank())
                ? Arrays.asList(entity.getRoles().split(","))
                : List.of("ROLE_API_KEY");

        return ApiKey.builder()
                .id(entity.getId())
                .projectId(entity.getProjectId())
                .name(entity.getName())
                .keyPrefix(entity.getKeyPrefix())
                .keyHash(entity.getKeyHash())
                .roles(roles)
                .revoked(entity.isRevoked())
                .revokedAt(entity.getRevokedAt())
                .expiresAt(entity.getExpiresAt())
                .lastUsedAt(entity.getLastUsedAt())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
