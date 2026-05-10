package com.spectrayan.promptly.prompt.application.service;

import com.spectrayan.promptly.prompt.application.port.in.GetPromptUseCase;
import com.spectrayan.promptly.prompt.application.port.out.PromptPersistencePort;
import com.spectrayan.promptly.prompt.domain.model.Prompt;
import com.spectrayan.promptly.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Application service for retrieving prompts.
 * Read-only operations — no side effects.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetPromptService implements GetPromptUseCase {

    private final PromptPersistencePort promptRepository;

    @Override
    public Mono<Prompt> getPromptById(String id) {
        return promptRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Prompt", id)));
    }

    @Override
    public Flux<Prompt> getAllPrompts() {
        return promptRepository.findAll();
    }

    @Override
    public Flux<Prompt> getAllPrompts(Pageable pageable) {
        return promptRepository.findAll(pageable);
    }

    @Override
    public Flux<Prompt> getPromptsByProjectId(String projectId) {
        return promptRepository.findByProjectId(projectId);
    }

    @Override
    public Flux<Prompt> getPromptsByProjectId(String projectId, Pageable pageable) {
        return promptRepository.findByProjectId(projectId, pageable);
    }
}

