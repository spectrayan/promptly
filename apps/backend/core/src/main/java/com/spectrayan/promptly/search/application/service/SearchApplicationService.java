package com.spectrayan.promptly.search.application.service;

import com.spectrayan.promptly.prompt.PromptModuleApi;
import com.spectrayan.promptly.search.application.port.in.SemanticSearchUseCase;
import com.spectrayan.promptly.search.application.port.out.EmbeddingPort;
import com.spectrayan.promptly.search.application.port.out.VectorSearchPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Application service orchestrating semantic search operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SearchApplicationService implements SemanticSearchUseCase {

    private final EmbeddingPort embeddingPort;
    private final VectorSearchPort vectorSearchPort;
    private final PromptModuleApi promptModuleApi;

    @Override
    public Flux<SearchResult> search(String query) {
        log.debug("Semantic search: query='{}'", query);

        return Mono.fromCallable(() -> embeddingPort.generateEmbedding(query))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(vector -> vectorSearchPort.searchByVector(vector, 10));
    }

    @Override
    public Flux<SearchResult> findSimilar(String promptId) {
        log.debug("Finding similar prompts: promptId={}", promptId);
        return vectorSearchPort.findSimilarByPromptId(promptId, 5);
    }

    @Override
    public Mono<DuplicateCheckResult> checkDuplicates(String promptId) {
        log.debug("Checking duplicates for prompt: {}", promptId);
        return findSimilar(promptId)
                .filter(r -> r.score() > 0.95)
                .collectList()
                .map(duplicates -> new DuplicateCheckResult(
                        promptId,
                        !duplicates.isEmpty(),
                        duplicates
                ));
    }

    /**
     * Generates and stores an embedding for a prompt.
     * Called by the event listener when a prompt is created/updated.
     */
    public Mono<Void> generateAndStoreEmbedding(String promptId) {
        return promptModuleApi.findById(promptId)
                .flatMap(prompt -> {
                    String content = (prompt.latestContent() != null && !prompt.latestContent().isBlank())
                            ? prompt.latestContent()
                            : prompt.name() + " " + (prompt.description() != null ? prompt.description() : "");

                    return Mono.fromCallable(() -> embeddingPort.generateEmbedding(content))
                            .subscribeOn(Schedulers.boundedElastic())
                            .flatMap(vector -> vectorSearchPort.saveEmbedding(promptId, vector));
                });
    }

}
