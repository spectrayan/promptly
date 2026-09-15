package com.spectrayan.promptly.auth.infrastructure.persistence.mongo.repository;

import com.spectrayan.promptly.auth.application.port.out.ApiKeyPersistencePort;
import com.spectrayan.promptly.auth.domain.model.ApiKey;
import com.spectrayan.promptly.auth.infrastructure.persistence.mongo.entity.ApiKeyDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@ConditionalOnProperty(name = "promptly.persistence.type", havingValue = "mongo", matchIfMissing = true)
@RequiredArgsConstructor
public class ApiKeyMongoAdapter implements ApiKeyPersistencePort {

    private final ApiKeyReactiveMongoRepository repository;

    @Override
    public Mono<ApiKey> save(ApiKey apiKey) {
        return repository.save(toDocument(apiKey)).map(this::toDomain);
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

    private ApiKeyDocument toDocument(ApiKey key) {
        return ApiKeyDocument.builder()
                .id(key.getId())
                .projectId(key.getProjectId())
                .name(key.getName())
                .keyPrefix(key.getKeyPrefix())
                .keyHash(key.getKeyHash())
                .roles(key.getRoles() != null ? key.getRoles() : List.of("ROLE_API_KEY"))
                .revoked(key.isRevoked())
                .revokedAt(key.getRevokedAt())
                .expiresAt(key.getExpiresAt())
                .lastUsedAt(key.getLastUsedAt())
                .createdBy(key.getCreatedBy())
                .createdAt(key.getCreatedAt())
                .updatedAt(key.getUpdatedAt())
                .build();
    }

    private ApiKey toDomain(ApiKeyDocument doc) {
        return ApiKey.builder()
                .id(doc.getId())
                .projectId(doc.getProjectId())
                .name(doc.getName())
                .keyPrefix(doc.getKeyPrefix())
                .keyHash(doc.getKeyHash())
                .roles(doc.getRoles() != null ? doc.getRoles() : List.of("ROLE_API_KEY"))
                .revoked(doc.isRevoked())
                .revokedAt(doc.getRevokedAt())
                .expiresAt(doc.getExpiresAt())
                .lastUsedAt(doc.getLastUsedAt())
                .createdBy(doc.getCreatedBy())
                .createdAt(doc.getCreatedAt())
                .updatedAt(doc.getUpdatedAt())
                .build();
    }
}
