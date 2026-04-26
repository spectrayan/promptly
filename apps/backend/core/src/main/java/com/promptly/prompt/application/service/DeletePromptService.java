package com.promptly.prompt.application.service;

import com.promptly.prompt.application.port.in.DeletePromptUseCase;
import com.promptly.prompt.application.port.out.PromptPersistencePort;
import com.promptly.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Application service for deleting prompts.
 * Business rule enforcement (isDeletable) is delegated to the Prompt aggregate.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeletePromptService implements DeletePromptUseCase {

    private final PromptPersistencePort promptRepository;

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
