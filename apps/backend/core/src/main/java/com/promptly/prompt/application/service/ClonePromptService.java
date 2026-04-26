package com.promptly.prompt.application.service;

import com.promptly.prompt.PromptCreated;
import com.promptly.prompt.application.port.in.ClonePromptUseCase;
import com.promptly.prompt.application.port.out.PromptPersistencePort;
import com.promptly.prompt.domain.model.Prompt;
import com.promptly.prompt.domain.model.PromptStatus;
import com.promptly.prompt.domain.model.PromptVersion;
import com.promptly.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashSet;

/**
 * Application service for cloning an existing prompt.
 * Cloning creates a new DRAFT prompt with the source's latest content.
 * Always allowed — any prompt can be cloned regardless of status.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClonePromptService implements ClonePromptUseCase {

    private final PromptPersistencePort promptRepository;
    private final ApplicationEventPublisher eventPublisher;

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
}
