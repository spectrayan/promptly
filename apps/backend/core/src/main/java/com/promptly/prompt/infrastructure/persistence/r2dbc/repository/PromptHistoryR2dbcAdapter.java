package com.promptly.prompt.infrastructure.persistence.r2dbc.repository;

import com.promptly.prompt.application.port.out.PromptHistoryPersistencePort;
import com.promptly.prompt.domain.model.PromptVersion;
import com.promptly.prompt.infrastructure.persistence.r2dbc.entity.PromptHistoryR2dbcEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * R2DBC adapter implementing {@link PromptHistoryPersistencePort} for PostgreSQL.
 */
@Component
@ConditionalOnProperty(name = "promptly.persistence.type", havingValue = "postgres")
@RequiredArgsConstructor
public class PromptHistoryR2dbcAdapter implements PromptHistoryPersistencePort {

    private final PromptHistoryR2dbcRepository repository;

    @Override
    public Mono<PromptVersion> save(String promptId, PromptVersion version) {
        return repository.save(toEntity(promptId, version)).map(this::toDomain);
    }

    @Override
    public Flux<PromptVersion> findByPromptId(String promptId) {
        return repository.findByPromptIdOrderByVersionNumberAsc(promptId).map(this::toDomain);
    }

    @Override
    public Mono<PromptVersion> findByPromptIdAndVersion(String promptId, int versionNumber) {
        return repository.findByPromptIdAndVersionNumber(promptId, versionNumber).map(this::toDomain);
    }

    @Override
    public Mono<Void> deleteByPromptId(String promptId) {
        return repository.deleteByPromptId(promptId);
    }

    // ── Mapping ─────────────────────────────────────────────────────

    private PromptHistoryR2dbcEntity toEntity(String promptId, PromptVersion version) {
        return PromptHistoryR2dbcEntity.builder()
                .promptId(promptId)
                .versionNumber(version.getVersionNumber())
                .content(version.getContent())
                .changeMessage(version.getChangeMessage())
                .createdBy(version.getCreatedBy())
                .createdAt(version.getCreatedAt())
                .build();
    }

    private PromptVersion toDomain(PromptHistoryR2dbcEntity entity) {
        return PromptVersion.builder()
                .versionNumber(entity.getVersionNumber())
                .content(entity.getContent())
                .changeMessage(entity.getChangeMessage())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
