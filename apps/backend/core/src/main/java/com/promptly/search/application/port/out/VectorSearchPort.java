package com.promptly.search.application.port.out;

import com.promptly.search.application.port.in.SemanticSearchUseCase;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Outbound port for vector search operations.
 */
public interface VectorSearchPort {

    Mono<Void> saveEmbedding(String promptId, float[] embedding);

    Flux<SemanticSearchUseCase.SearchResult> searchByVector(float[] queryVector, int limit);

    Flux<SemanticSearchUseCase.SearchResult> findSimilarByPromptId(String promptId, int limit);

}
