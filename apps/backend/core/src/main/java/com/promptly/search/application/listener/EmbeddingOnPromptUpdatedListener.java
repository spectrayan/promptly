package com.promptly.search.application.listener;

import com.promptly.prompt.PromptCreated;
import com.promptly.prompt.PromptUpdated;
import com.promptly.search.application.service.SearchApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Listens for prompt events and generates/updates embeddings.
 * Uses @Async to avoid blocking the source module's reactive chain.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingOnPromptUpdatedListener {

    private final SearchApplicationService searchApplicationService;

    @Async
    @EventListener
    void on(PromptCreated event) {
        log.info("Generating embedding for new prompt: {}", event.aggregateId());
        searchApplicationService.generateAndStoreEmbedding(event.aggregateId())
                .doOnError(e -> log.warn("Embedding generation failed for prompt {}: {}", event.aggregateId(), e.getMessage()))
                .onErrorComplete()
                .subscribe();
    }

    @Async
    @EventListener
    void on(PromptUpdated event) {
        log.info("Updating embedding for prompt: {}", event.aggregateId());
        searchApplicationService.generateAndStoreEmbedding(event.aggregateId())
                .doOnError(e -> log.warn("Embedding update failed for prompt {}: {}", event.aggregateId(), e.getMessage()))
                .onErrorComplete()
                .subscribe();
    }

}
