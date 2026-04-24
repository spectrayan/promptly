package com.promptly.improver.infrastructure.web;

import com.promptly.infrastructure.in.web.api.ImproverApi;
import com.promptly.infrastructure.in.web.dto.ApplyImprovementRequest;
import com.promptly.infrastructure.in.web.dto.ImprovementResponse;
import com.promptly.improver.application.port.in.ImprovePromptUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * REST controller for the Quality Improver module.
 * Implements the contract-first {@link ImproverApi} interface generated from the OpenAPI specification.
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

}
