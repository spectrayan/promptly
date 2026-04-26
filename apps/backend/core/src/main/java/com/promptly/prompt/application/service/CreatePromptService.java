package com.promptly.prompt.application.service;

import com.promptly.prompt.PromptCreated;
import com.promptly.prompt.application.port.in.CreatePromptUseCase;
import com.promptly.prompt.application.port.out.PromptPersistencePort;
import com.promptly.prompt.domain.model.ContentFormat;
import com.promptly.prompt.domain.model.Prompt;
import com.promptly.prompt.domain.model.PromptStatus;
import com.promptly.shared.exception.DuplicateResourceException;
import com.promptly.shared.exception.ErrorCode;
import com.promptly.shared.exception.ErrorMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashSet;

/**
 * Application service for creating new prompts.
 * Enforces uniqueness invariant (name + project) and publishes {@link PromptCreated}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CreatePromptService implements CreatePromptUseCase {

    private final PromptPersistencePort promptRepository;
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
}
