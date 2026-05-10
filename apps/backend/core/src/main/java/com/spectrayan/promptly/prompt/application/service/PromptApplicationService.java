package com.spectrayan.promptly.prompt.application.service;

import com.spectrayan.promptly.prompt.PromptModuleApi;
import com.spectrayan.promptly.prompt.PromptProjection;
import com.spectrayan.promptly.prompt.application.port.in.UpdatePromptUseCase;
import com.spectrayan.promptly.prompt.application.port.out.PromptPersistencePort;
import com.spectrayan.promptly.prompt.domain.model.Prompt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implements the {@link PromptModuleApi} — the public API for cross-module access.
 * <p>
 * Returns {@link PromptProjection} DTOs to prevent leaking domain aggregates
 * across module boundaries.
 * <p>
 * Individual use case implementations live in their own dedicated service classes:
 * <ul>
 *   <li>{@link CreatePromptService} → {@link com.spectrayan.promptly.prompt.application.port.in.CreatePromptUseCase}</li>
 *   <li>{@link UpdatePromptService} → {@link com.spectrayan.promptly.prompt.application.port.in.UpdatePromptUseCase}</li>
 *   <li>{@link GetPromptService} → {@link com.spectrayan.promptly.prompt.application.port.in.GetPromptUseCase}</li>
 *   <li>{@link RollbackPromptService} → {@link com.spectrayan.promptly.prompt.application.port.in.RollbackPromptUseCase}</li>
 *   <li>{@link DeletePromptService} → {@link com.spectrayan.promptly.prompt.application.port.in.DeletePromptUseCase}</li>
 *   <li>{@link ClonePromptService} → {@link com.spectrayan.promptly.prompt.application.port.in.ClonePromptUseCase}</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PromptApplicationService implements PromptModuleApi {

    private final PromptPersistencePort promptRepository;
    private final UpdatePromptService updatePromptService;

    @Override
    public Mono<PromptProjection> findById(String id) {
        return promptRepository.findById(id)
                .map(this::toProjection);
    }

    @Override
    public Flux<PromptProjection> findByProjectId(String projectId) {
        return promptRepository.findByProjectId(projectId)
                .map(this::toProjection);
    }

    @Override
    public Flux<PromptProjection> findAll() {
        return promptRepository.findAll()
                .map(this::toProjection);
    }

    @Override
    public Mono<PromptProjection> updatePrompt(String id, String content, String changeMessage, String author) {
        return updatePromptService.updatePrompt(id,
                new UpdatePromptUseCase.UpdatePromptCommand(content, changeMessage, author))
                .map(this::toProjection);
    }

    // ── Mapping ──────────────────────────────────────────────────

    private PromptProjection toProjection(Prompt p) {
        return new PromptProjection(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getProjectId(),
                p.getStatus() != null ? p.getStatus().name() : null,
                p.getCurrentVersion(),
                p.getContent(),
                p.getContentFormat() != null ? p.getContentFormat().name() : null,
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}

