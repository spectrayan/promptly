package com.spectrayan.promptly.llmconfig.infrastructure.persistence.mongo.repository;

import com.spectrayan.promptly.llmconfig.application.port.out.LlmConfigPersistencePort;
import com.spectrayan.promptly.llmconfig.domain.model.LlmConfig;
import com.spectrayan.promptly.llmconfig.infrastructure.persistence.mongo.entity.LlmConfigDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * MongoDB adapter implementing the LlmConfigPersistencePort outbound port.
 */
@Repository
@ConditionalOnProperty(name = "promptly.persistence.type", havingValue = "mongo", matchIfMissing = true)
@RequiredArgsConstructor
public class LlmConfigMongoAdapter implements LlmConfigPersistencePort {

    private final LlmConfigReactiveMongoRepository mongoRepo;

    @Override
    public Mono<LlmConfig> findByProjectIdAndFeature(String projectId, String feature) {
        return mongoRepo.findByProjectIdAndFeature(projectId, feature)
                .map(this::toDomain);
    }

    @Override
    public Flux<LlmConfig> findByProjectId(String projectId) {
        return mongoRepo.findByProjectId(projectId)
                .map(this::toDomain);
    }

    @Override
    public Mono<LlmConfig> save(LlmConfig config) {
        return mongoRepo.findByProjectIdAndFeature(config.getProjectId(), config.getFeature())
                .map(existing -> {
                    // Update existing document
                    if (config.getProvider() != null) existing.setProvider(config.getProvider());
                    if (config.getModel() != null) existing.setModel(config.getModel());
                    if (config.getTemperature() != null) existing.setTemperature(config.getTemperature());
                    if (config.getMaxTokens() != null) existing.setMaxTokens(config.getMaxTokens());
                    if (config.getBaseUrl() != null) existing.setBaseUrl(config.getBaseUrl());
                    if (config.getEncryptedApiKey() != null) existing.setEncryptedApiKey(config.getEncryptedApiKey());
                    existing.setUpdatedAt(config.getUpdatedAt());
                    existing.setUpdatedBy(config.getUpdatedBy());
                    return existing;
                })
                .switchIfEmpty(Mono.defer(() -> Mono.just(toDocument(config))))
                .flatMap(mongoRepo::save)
                .map(this::toDomain);
    }

    @Override
    public Mono<Void> deleteByProjectIdAndFeature(String projectId, String feature) {
        return mongoRepo.deleteByProjectIdAndFeature(projectId, feature);
    }

    private LlmConfig toDomain(LlmConfigDocument doc) {
        return LlmConfig.builder()
                .id(doc.getId())
                .projectId(doc.getProjectId())
                .feature(doc.getFeature())
                .provider(doc.getProvider())
                .model(doc.getModel())
                .temperature(doc.getTemperature())
                .maxTokens(doc.getMaxTokens())
                .baseUrl(doc.getBaseUrl())
                .encryptedApiKey(doc.getEncryptedApiKey())
                .updatedAt(doc.getUpdatedAt())
                .updatedBy(doc.getUpdatedBy())
                .build();
    }

    private LlmConfigDocument toDocument(LlmConfig config) {
        return LlmConfigDocument.builder()
                .projectId(config.getProjectId())
                .feature(config.getFeature())
                .provider(config.getProvider())
                .model(config.getModel())
                .temperature(config.getTemperature())
                .maxTokens(config.getMaxTokens())
                .baseUrl(config.getBaseUrl())
                .encryptedApiKey(config.getEncryptedApiKey())
                .updatedAt(config.getUpdatedAt())
                .updatedBy(config.getUpdatedBy())
                .build();
    }
}
