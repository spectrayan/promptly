package com.promptly.prompt.infrastructure.persistence.r2dbc.repository;

import com.promptly.prompt.application.port.out.PromptPersistencePort;
import com.promptly.prompt.domain.model.*;
import com.promptly.prompt.infrastructure.persistence.r2dbc.entity.PromptR2dbcEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

/**
 * R2DBC adapter implementing {@link PromptPersistencePort} for SQL databases.
 * <p>
 * No {@code ObjectMapper} needed — JSON columns use typed entity fields
 * and R2DBC converters handle serialization transparently.
 */
@Component
@ConditionalOnProperty(name = "promptly.persistence.type", havingValue = "sql")
@RequiredArgsConstructor
public class PromptR2dbcAdapter implements PromptPersistencePort {

    private final PromptR2dbcRepository repository;

    @Override
    public Mono<Prompt> save(Prompt prompt) {
        return repository.save(toEntity(prompt)).map(this::toDomain);
    }

    @Override
    public Mono<Prompt> findById(String id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Flux<Prompt> findByProjectId(String projectId) {
        return repository.findByProjectId(projectId).map(this::toDomain);
    }

    @Override
    public Flux<Prompt> findByProjectId(String projectId, Pageable pageable) {
        return repository.findByProjectId(projectId, pageable).map(this::toDomain);
    }

    @Override
    public Flux<Prompt> findAll() {
        return repository.findAll().map(this::toDomain);
    }

    @Override
    public Flux<Prompt> findAll(Pageable pageable) {
        return repository.findAllBy(pageable).map(this::toDomain);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }

    @Override
    public Mono<Boolean> existsByNameAndProjectId(String name, String projectId) {
        return repository.existsByNameAndProjectId(name, projectId);
    }

    // ── Mapping ─────────────────────────────────────────────────────

    private PromptR2dbcEntity toEntity(Prompt prompt) {
        PromptMetadata md = prompt.getMetadata();

        return PromptR2dbcEntity.builder()
                .id(prompt.getId())
                .name(prompt.getName())
                .description(prompt.getDescription())
                .projectId(prompt.getProjectId())
                .contentFormat(prompt.getContentFormat() != null ? prompt.getContentFormat().name() : null)
                .tags(prompt.getTags() != null ? prompt.getTags() : Set.of())
                .metadataModel(md != null ? md.getModel() : null)
                .metadataTemperature(md != null ? md.getTemperature() : null)
                .metadataMaxTokens(md != null ? md.getMaxTokens() : null)
                .metadataSystemContext(md != null ? md.getSystemContext() : null)
                .currentVersion(prompt.getCurrentVersion())
                .status(prompt.getStatus() != null ? prompt.getStatus().name() : PromptStatus.DRAFT.name())
                .version(prompt.getVersion() != null && prompt.getVersion() > 0 ? prompt.getVersion() : null)
                .createdAt(prompt.getCreatedAt())
                .updatedAt(prompt.getUpdatedAt())
                .createdBy(prompt.getCreatedBy())
                .updatedBy(prompt.getUpdatedBy())
                .build();
    }

    private Prompt toDomain(PromptR2dbcEntity entity) {
        PromptMetadata metadata = null;
        if (entity.getMetadataModel() != null || entity.getMetadataTemperature() != null
                || entity.getMetadataMaxTokens() != null || entity.getMetadataSystemContext() != null) {
            metadata = PromptMetadata.builder()
                    .model(entity.getMetadataModel())
                    .temperature(entity.getMetadataTemperature())
                    .maxTokens(entity.getMetadataMaxTokens())
                    .systemContext(entity.getMetadataSystemContext())
                    .build();
        }

        return Prompt.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .projectId(entity.getProjectId())
                .contentFormat(entity.getContentFormat() != null ? ContentFormat.valueOf(entity.getContentFormat()) : null)
                .tags(entity.getTags() != null ? entity.getTags() : Set.of())
                .metadata(metadata)
                .currentVersion(entity.getCurrentVersion())
                .status(entity.getStatus() != null ? PromptStatus.valueOf(entity.getStatus()) : PromptStatus.DRAFT)
                .version(entity.getVersion())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}
