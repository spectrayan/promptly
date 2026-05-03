package com.promptly.prompt.application.service;

import com.promptly.prompt.application.port.in.DeletePromptUseCase;
import com.promptly.prompt.application.port.out.PromptHistoryPersistencePort;
import com.promptly.prompt.application.port.out.PromptPersistencePort;
import com.promptly.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Application service for deleting prompts.
 * Business rule enforcement (isDeletable) is delegated to the Prompt aggregate.
 * <p>
 * Cascades the delete to the {@code prompt_history} collection/table via
 * {@link PromptHistoryPersistencePort}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeletePromptService implements DeletePromptUseCase {

    private final PromptPersistencePort promptRepository;
    private final PromptHistoryPersistencePort historyRepository;

    @Override
    public Mono<Void> deletePrompt(String id) {
        log.info("Deleting prompt: id={}", id);
        return promptRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Prompt", id)))
                .flatMap(prompt -> {
                    // Business rule check
                    prompt.assertDeletable();
                    // Cascade: delete version history first, then the prompt
                    return historyRepository.deleteByPromptId(id)
                            .then(promptRepository.deleteById(id));
                });
    }
}
