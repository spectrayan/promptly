package com.spectrayan.promptly.improver.infrastructure.web;

import com.spectrayan.promptly.infrastructure.in.web.api.ImproverApi;
import com.spectrayan.promptly.infrastructure.in.web.dto.ApplyImprovementRequest;
import com.spectrayan.promptly.infrastructure.in.web.dto.GenerateFromIdeaRequest;
import com.spectrayan.promptly.infrastructure.in.web.dto.GenerateFromIdeaResponse;
import com.spectrayan.promptly.infrastructure.in.web.dto.ImprovementResponse;
import com.spectrayan.promptly.improver.application.port.in.ImprovePromptUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * REST controller for the Quality Improver module.
 * Implements the contract-first {@link ImproverApi} interface generated from the OpenAPI specification.
 * Also handles the {@code generateFromIdea} endpoint (moved here from PromptController
 * to avoid a module cycle: prompt → improver → prompt).
 */
@RestController
@RequiredArgsConstructor
public class ImproverController implements ImproverApi {

    private final ImprovePromptUseCase improvePromptUseCase;

    @Override
    public Mono<ResponseEntity<ImprovementResponse>> improvePrompt(
            String promptId, ServerWebExchange exchange) {
        return improvePromptUseCase.improvePrompt(promptId)
                .map(r -> {
                    var response = new ImprovementResponse();
                    response.setPromptId(r.promptId());
                    response.setOriginalContent(r.originalContent());
                    response.setImprovedContent(r.improvedContent());
                    response.setSummary(r.summary());
                    return response;
                })
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Void>> applyImprovement(
            String promptId, Mono<ApplyImprovementRequest> applyImprovementRequest, ServerWebExchange exchange) {
        return applyImprovementRequest
                .flatMap(req -> improvePromptUseCase.applyImprovement(
                        promptId, req.getImprovedContent(), req.getAuthor()
                ))
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }

    /**
     * Generates a complete prompt from a short idea description.
     * <p>
     * This endpoint is hosted here (in the improver module) rather than in
     * PromptController to avoid a circular module dependency.
     */
    @Override
    public Mono<ResponseEntity<GenerateFromIdeaResponse>> generateFromIdea(
            Mono<GenerateFromIdeaRequest> generateFromIdeaRequest, ServerWebExchange exchange) {
        return generateFromIdeaRequest
                .flatMap(req -> improvePromptUseCase.generateFromIdea(req.getIdea()))
                .map(result -> {
                    var response = new GenerateFromIdeaResponse();
                    response.setGeneratedContent(result.generatedContent());
                    response.setTitle(result.title());
                    response.setSummary(result.summary());
                    return response;
                })
                .map(ResponseEntity::ok);
    }

}

