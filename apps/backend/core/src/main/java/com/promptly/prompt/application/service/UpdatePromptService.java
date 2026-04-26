package com.promptly.prompt.application.service;

import com.promptly.prompt.PromptUpdated;
import com.promptly.prompt.application.port.in.UpdatePromptUseCase;
import com.promptly.prompt.application.port.out.PromptPersistencePort;
import com.promptly.prompt.domain.model.Prompt;
import com.promptly.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Application service for updating existing prompts.
 * Creates a new version — content is never overwritten.
 * Business rule enforcement (isEditable) is delegated to the Prompt aggregate.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UpdatePromptService implements UpdatePromptUseCase {

    private final PromptPersistencePort promptRepository;
    private final ApplicationEventPublisher eventPublisher;

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
}
