package com.promptly.search.application.port.in;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Inbound port for semantic search operations.
 */
public interface SemanticSearchUseCase {

    Flux<SearchResult> search(String query);

    Flux<SearchResult> findSimilar(String promptId);

    Mono<DuplicateCheckResult> checkDuplicates(String promptId);

    record SearchResult(
            String promptId,
            String name,
            String description,
            double score
    ) {}

    record DuplicateCheckResult(
            String promptId,
            boolean hasDuplicates,
            java.util.List<SearchResult> duplicates
    ) {}

}
