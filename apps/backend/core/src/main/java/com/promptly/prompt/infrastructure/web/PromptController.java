package com.promptly.prompt.infrastructure.web;

import com.promptly.infrastructure.in.web.api.PromptsApi;
import com.promptly.infrastructure.in.web.dto.CreatePromptRequest;
import com.promptly.infrastructure.in.web.dto.PromptResponse;
import com.promptly.infrastructure.in.web.dto.PromptSummaryResponse;
import com.promptly.infrastructure.in.web.dto.UpdatePromptRequest;
import com.promptly.infrastructure.in.web.dto.VersionResponse;
import com.promptly.prompt.application.port.in.*;
import com.promptly.prompt.domain.model.Prompt;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * REST controller for the Prompt Registry module.
 * Implements the contract-first {@link PromptsApi} interface generated from the OpenAPI specification.
 */
@RestController
@RequiredArgsConstructor
public class PromptController implements PromptsApi {

    private final CreatePromptUseCase createPromptUseCase;
    private final UpdatePromptUseCase updatePromptUseCase;
    private final GetPromptUseCase getPromptUseCase;
    private final RollbackPromptUseCase rollbackPromptUseCase;
    private final DeletePromptUseCase deletePromptUseCase;
    private final PromptWebMapper mapper;

    @Override
    public Mono<ResponseEntity<PromptResponse>> createPrompt(
            Mono<CreatePromptRequest> createPromptRequest, ServerWebExchange exchange) {
        return createPromptRequest
                .map(req -> new CreatePromptUseCase.CreatePromptCommand(
                        req.getName(),
                        req.getDescription(),
                        req.getProjectId(),
                        req.getContentFormat() != null ? req.getContentFormat().getValue() : null,
                        req.getContent(),
                        req.getAuthor()
                ))
                .flatMap(createPromptUseCase::createPrompt)
                .map(mapper::toPromptResponse)
                .map(r -> ResponseEntity.status(HttpStatus.CREATED).body(r));
    }

    @Override
    public Mono<ResponseEntity<Flux<PromptSummaryResponse>>> listPrompts(
            String projectId, ServerWebExchange exchange) {
        Flux<Prompt> prompts = (projectId != null)
                ? getPromptUseCase.getPromptsByProjectId(projectId)
                : getPromptUseCase.getAllPrompts();
        return Mono.just(ResponseEntity.ok(prompts.map(mapper::toSummaryResponse)));
    }

    @Override
    public Mono<ResponseEntity<PromptResponse>> getPrompt(
            String id, ServerWebExchange exchange) {
        return getPromptUseCase.getPromptById(id)
                .map(mapper::toPromptResponse)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<PromptResponse>> updatePrompt(
            String id, Mono<UpdatePromptRequest> updatePromptRequest, ServerWebExchange exchange) {
        return updatePromptRequest
                .map(req -> new UpdatePromptUseCase.UpdatePromptCommand(
                        req.getContent(),
                        req.getChangeMessage(),
                        req.getAuthor()
                ))
                .flatMap(cmd -> updatePromptUseCase.updatePrompt(id, cmd))
                .map(mapper::toPromptResponse)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Flux<VersionResponse>>> getVersionHistory(
            String id, ServerWebExchange exchange) {
        return getPromptUseCase.getPromptById(id)
                .map(prompt -> ResponseEntity.ok(
                        Flux.fromIterable(prompt.getVersions())
                                .map(mapper::toVersionResponse)
                ));
    }

    @Override
    public Mono<ResponseEntity<VersionResponse>> getSpecificVersion(
            String id, Integer versionNumber, ServerWebExchange exchange) {
        return getPromptUseCase.getPromptById(id)
                .map(prompt -> prompt.getVersion(versionNumber))
                .map(mapper::toVersionResponse)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<PromptResponse>> rollbackPrompt(
            String id, Integer targetVersion, String author, ServerWebExchange exchange) {
        return rollbackPromptUseCase.rollbackToVersion(id, targetVersion, author)
                .map(mapper::toPromptResponse)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Void>> deletePrompt(
            String id, ServerWebExchange exchange) {
        return deletePromptUseCase.deletePrompt(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }

}
