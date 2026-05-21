package com.spectrayan.promptly.search.infrastructure.web;

import com.spectrayan.promptly.infrastructure.in.web.api.SearchApi;
import com.spectrayan.promptly.infrastructure.in.web.dto.DuplicateCheckResponse;
import com.spectrayan.promptly.infrastructure.in.web.dto.SearchResponse;
import com.spectrayan.promptly.search.application.port.in.SemanticSearchUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * REST controller for the Semantic Search module.
 * Implements the contract-first {@link SearchApi} interface generated from the OpenAPI specification.
 */
@RestController
@RequiredArgsConstructor
public class SearchController implements SearchApi {

    private final SemanticSearchUseCase semanticSearchUseCase;

    @Override
    public Mono<ResponseEntity<Flux<SearchResponse>>> searchPrompts(
            String q, ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(
                semanticSearchUseCase.search(q)
                        .map(this::toSearchResponse)
        ));
    }

    @Override
    public Mono<ResponseEntity<Flux<SearchResponse>>> findSimilarPrompts(
            String promptId, ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(
                semanticSearchUseCase.findSimilar(promptId)
                        .map(this::toSearchResponse)
        ));
    }

    @Override
    public Mono<ResponseEntity<DuplicateCheckResponse>> checkDuplicates(
            String promptId, ServerWebExchange exchange) {
        return semanticSearchUseCase.checkDuplicates(promptId)
                .map(r -> {
                    var response = new DuplicateCheckResponse();
                    response.setPromptId(r.promptId());
                    response.setHasDuplicates(r.hasDuplicates());
                    response.setDuplicates(
                            r.duplicates().stream()
                                    .map(this::toSearchResponse)
                                    .toList()
                    );
                    return response;
                })
                .map(ResponseEntity::ok);
    }

    // ── Domain → DTO mapping ──────────────────────────────────────────

    private SearchResponse toSearchResponse(SemanticSearchUseCase.SearchResult result) {
        var response = new SearchResponse();
        response.setPromptId(result.promptId());
        response.setName(result.name());
        response.setDescription(result.description());
        response.setScore(result.score());
        return response;
    }

}
