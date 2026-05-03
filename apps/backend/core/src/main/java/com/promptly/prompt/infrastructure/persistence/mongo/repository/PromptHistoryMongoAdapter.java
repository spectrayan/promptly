package com.promptly.prompt.infrastructure.persistence.mongo.repository;

import com.promptly.prompt.application.port.out.PromptHistoryPersistencePort;
import com.promptly.prompt.domain.model.PromptVersion;
import com.promptly.prompt.infrastructure.persistence.mongo.entity.PromptHistoryDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * MongoDB adapter implementing {@link PromptHistoryPersistencePort}.
 * <p>
 * Persists prompt versions as individual documents in the {@code prompt_history}
 * collection, replacing the previous embedded-array approach.
 */
@Component
@ConditionalOnProperty(name = "promptly.persistence.type", havingValue = "mongo", matchIfMissing = true)
@RequiredArgsConstructor
public class PromptHistoryMongoAdapter implements PromptHistoryPersistencePort {

    private final PromptHistoryReactiveMongoRepository repository;

    @Override
    public Mono<PromptVersion> save(String promptId, PromptVersion version) {
        var document = PromptHistoryDocument.builder()
                .promptId(promptId)
                .versionNumber(version.getVersionNumber())
                .content(version.getContent())
                .changeMessage(version.getChangeMessage())
                .createdBy(version.getCreatedBy())
                .createdAt(version.getCreatedAt())
                .build();
        return repository.save(document)
                .map(this::toDomain);
    }

    @Override
    public Flux<PromptVersion> findByPromptId(String promptId) {
        return repository.findByPromptIdOrderByVersionNumberAsc(promptId)
                .map(this::toDomain);
    }

    @Override
    public Mono<PromptVersion> findByPromptIdAndVersion(String promptId, int versionNumber) {
        return repository.findByPromptIdAndVersionNumber(promptId, versionNumber)
                .map(this::toDomain);
    }

    @Override
    public Mono<Void> deleteByPromptId(String promptId) {
        return repository.deleteByPromptId(promptId);
    }

    private PromptVersion toDomain(PromptHistoryDocument doc) {
        return PromptVersion.builder()
                .versionNumber(doc.getVersionNumber())
                .content(doc.getContent())
                .changeMessage(doc.getChangeMessage())
                .createdBy(doc.getCreatedBy())
                .createdAt(doc.getCreatedAt())
                .build();
    }
}
