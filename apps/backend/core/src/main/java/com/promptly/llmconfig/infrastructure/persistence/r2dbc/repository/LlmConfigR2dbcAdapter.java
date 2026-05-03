package com.promptly.llmconfig.infrastructure.persistence.r2dbc.repository;

import com.promptly.llmconfig.application.port.out.LlmConfigPersistencePort;
import com.promptly.llmconfig.domain.model.LlmConfig;
import com.promptly.llmconfig.infrastructure.persistence.r2dbc.entity.LlmConfigR2dbcEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * R2DBC adapter implementing {@link LlmConfigPersistencePort} for PostgreSQL.
 */
@Component
@ConditionalOnProperty(name = "promptly.persistence.type", havingValue = "postgres")
@RequiredArgsConstructor
public class LlmConfigR2dbcAdapter implements LlmConfigPersistencePort {

    private final LlmConfigR2dbcRepository repository;

    @Override
    public Mono<LlmConfig> findByProjectIdAndFeature(String projectId, String feature) {
        return repository.findByProjectIdAndFeature(projectId, feature).map(this::toDomain);
    }

    @Override
    public Flux<LlmConfig> findByProjectId(String projectId) {
        return repository.findByProjectId(projectId).map(this::toDomain);
    }

    @Override
    public Mono<LlmConfig> save(LlmConfig config) {
        return repository.save(toEntity(config)).map(this::toDomain);
    }

    @Override
    public Mono<Void> deleteByProjectIdAndFeature(String projectId, String feature) {
        return repository.deleteByProjectIdAndFeature(projectId, feature);
    }

    // ── Mapping ─────────────────────────────────────────────────────

    private LlmConfigR2dbcEntity toEntity(LlmConfig config) {
        return LlmConfigR2dbcEntity.builder()
                .projectId(config.getProjectId())
                .feature(config.getFeature())
                .provider(config.getProvider())
                .model(config.getModel())
                .encryptedApiKey(config.getEncryptedApiKey())
                .baseUrl(config.getBaseUrl())
                .temperature(config.getTemperature())
                .maxTokens(config.getMaxTokens())
                .updatedAt(config.getUpdatedAt())
                .updatedBy(config.getUpdatedBy())
                .build();
    }

    private LlmConfig toDomain(LlmConfigR2dbcEntity entity) {
        return LlmConfig.builder()
                .id(entity.getId())
                .projectId(entity.getProjectId())
                .feature(entity.getFeature())
                .provider(entity.getProvider())
                .model(entity.getModel())
                .encryptedApiKey(entity.getEncryptedApiKey())
                .baseUrl(entity.getBaseUrl())
                .temperature(entity.getTemperature())
                .maxTokens(entity.getMaxTokens())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}
