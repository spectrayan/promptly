package com.promptly.prompt.application.service;

import com.promptly.prompt.PromptCreated;
import com.promptly.prompt.PromptModuleApi;
import com.promptly.prompt.PromptRolledBack;
import com.promptly.prompt.PromptUpdated;
import com.promptly.prompt.application.port.in.*;
import com.promptly.prompt.application.port.out.PromptRepository;
import com.promptly.prompt.domain.model.ContentFormat;
import com.promptly.prompt.domain.model.Prompt;
import com.promptly.prompt.domain.model.PromptSpecifications;
import com.promptly.prompt.domain.model.PromptStatus;
import com.promptly.prompt.domain.model.PromptVersion;
import com.promptly.shared.exception.DuplicateResourceException;
import com.promptly.shared.exception.ErrorCode;
import com.promptly.shared.exception.ErrorMessages;
import com.promptly.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;

/**
 * Application service orchestrating prompt use cases.
 * Business rules are enforced by the Prompt aggregate root via Specification pattern.
 * Also implements PromptModuleApi — the public API for cross-module access.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PromptApplicationService implements
        CreatePromptUseCase,
        UpdatePromptUseCase,
        GetPromptUseCase,
        RollbackPromptUseCase,
        DeletePromptUseCase,
        ClonePromptUseCase,
        PromptModuleApi {

    private final PromptRepository promptRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Mono<Prompt> createPrompt(CreatePromptCommand command) {
        log.info("Creating prompt: {}", command.name());

        return promptRepository.existsByNameAndProjectId(command.name(), command.projectId())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.<Prompt>error(new DuplicateResourceException(
                                ErrorCode.PROMPT_DUPLICATE_NAME,
                                String.format(ErrorMessages.DUPLICATE_PROMPT_NAME, command.name())));
                    }

                    Prompt prompt = Prompt.builder()
                            .name(command.name())
                            .description(command.description())
                            .projectId(command.projectId())
                            .contentFormat(command.contentFormat() != null ? ContentFormat.valueOf(command.contentFormat()) : ContentFormat.TEXT)
                            .tags(new HashSet<>())
                            .currentVersion(0)
                            .status(PromptStatus.DRAFT)
                            .build();

                    // createNewVersion checks isEditable — DRAFT status is always allowed
                    prompt.createNewVersion(command.content(), "Initial version", command.author());

                    return promptRepository.save(prompt)
                            .doOnSuccess(saved -> {
                                log.info("Prompt created: id={}, name={}", saved.getId(), saved.getName());
                                eventPublisher.publishEvent(
                                        new PromptCreated(saved.getId(), saved.getName(), saved.getProjectId(), saved.getCurrentVersion())
                                );
                            });
                });
    }

    /**
     * Updates a prompt by creating a new version.
     * Business rule: only DRAFT prompts not in review can be edited.
     * Enforcement is done inside Prompt.createNewVersion() via PromptSpecifications.isEditable().
     */
    @Override
    public Mono<Prompt> updatePrompt(String id, UpdatePromptCommand command) {
        log.info("Updating prompt: id={}", id);
        return promptRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Prompt", id)))
                .flatMap(prompt -> {
                    // Business rule check happens inside createNewVersion
                    prompt.createNewVersion(command.content(), command.changeMessage(), command.author());
                    return promptRepository.save(prompt);
                })
                .doOnSuccess(saved -> {
                    log.info("Prompt updated: id={}, version={}", saved.getId(), saved.getCurrentVersion());
                    eventPublisher.publishEvent(
                            new PromptUpdated(saved.getId(), saved.getProjectId(), saved.getCurrentVersion())
                    );
                });
    }

    // PromptModuleApi implementation (simplified for cross-module use)
    @Override
    public Mono<Prompt> updatePrompt(String id, String content, String changeMessage, String author) {
        return updatePrompt(id, new UpdatePromptCommand(content, changeMessage, author));
    }

    /**
     * Clones a prompt into a new DRAFT prompt with the source's latest content.
     * Always allowed — any prompt can be cloned regardless of status.
     */
    @Override
    public Mono<Prompt> clonePrompt(String sourcePromptId, ClonePromptCommand command) {
        log.info("Cloning prompt: sourceId={}, newName={}", sourcePromptId, command.name());
        return promptRepository.findById(sourcePromptId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Prompt", sourcePromptId)))
                .flatMap(source -> {
                    // Get latest content from source
                    PromptVersion latestVersion = source.getVersion(source.getCurrentVersion());

                    Prompt clone = Prompt.builder()
                            .name(command.name())
                            .description(command.description() != null
                                    ? command.description()
                                    : "Cloned from: " + source.getName())
                            .projectId(command.projectId() != null
                                    ? command.projectId()
                                    : source.getProjectId())
                            .contentFormat(source.getContentFormat())
                            .tags(source.getTags() != null ? new HashSet<>(source.getTags()) : new HashSet<>())
                            .metadata(source.getMetadata())
                            .currentVersion(0)
                            .status(PromptStatus.DRAFT)
                            .build();

                    clone.createNewVersion(
                            latestVersion.getContent(),
                            "Cloned from " + source.getName() + " v" + source.getCurrentVersion(),
                            command.author()
                    );

                    return promptRepository.save(clone);
                })
                .doOnSuccess(saved -> {
                    log.info("Prompt cloned: newId={}, name={}", saved.getId(), saved.getName());
                    eventPublisher.publishEvent(
                            new PromptCreated(saved.getId(), saved.getName(), saved.getProjectId(), saved.getCurrentVersion())
                    );
                });
    }

    @Override
    public Mono<Prompt> getPromptById(String id) {
        return promptRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Prompt", id)));
    }

    // PromptModuleApi alias
    @Override
    public Mono<Prompt> findById(String id) {
        return promptRepository.findById(id);
    }

    @Override
    public Flux<Prompt> getAllPrompts() {
        return promptRepository.findAll();
    }

    // PromptModuleApi alias
    @Override
    public Flux<Prompt> findAll() {
        return promptRepository.findAll();
    }

    @Override
    public Flux<Prompt> getPromptsByProjectId(String projectId) {
        return promptRepository.findByProjectId(projectId);
    }

    // PromptModuleApi alias
    @Override
    public Flux<Prompt> findByProjectId(String projectId) {
        return promptRepository.findByProjectId(projectId);
    }

    /**
     * Rolls back a prompt to a previous version.
     * Business rule: only DRAFT prompts not in review can be rolled back.
     */
    @Override
    public Mono<Prompt> rollbackToVersion(String id, int targetVersion, String author) {
        log.info("Rolling back prompt {} to version {}", id, targetVersion);
        return promptRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Prompt", id)))
                .flatMap(prompt -> {
                    // Business rule check
                    prompt.assertRollbackable();

                    int fromVersion = prompt.getCurrentVersion();
                    PromptVersion target = prompt.getVersion(targetVersion);

                    // createNewVersion checks isEditable, which is satisfied for DRAFT status
                    prompt.createNewVersion(
                            target.getContent(),
                            "Rollback to version " + targetVersion,
                            author
                    );
                    return promptRepository.save(prompt)
                            .doOnSuccess(saved -> {
                                log.info("Prompt rolled back: id={}, from={}, to={}", id, fromVersion, targetVersion);
                                eventPublisher.publishEvent(
                                        new PromptRolledBack(saved.getId(), fromVersion, targetVersion)
                                );
                            });
                });
    }

    /**
     * Deletes a prompt.
     * Business rule: only DRAFT prompts not in review can be deleted.
     */
    @Override
    public Mono<Void> deletePrompt(String id) {
        log.info("Deleting prompt: id={}", id);
        return promptRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Prompt", id)))
                .flatMap(prompt -> {
                    // Business rule check
                    prompt.assertDeletable();
                    return promptRepository.deleteById(id);
                });
    }

}
