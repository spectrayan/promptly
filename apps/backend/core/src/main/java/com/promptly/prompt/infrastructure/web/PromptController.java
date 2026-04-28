package com.promptly.prompt.infrastructure.web;

import com.promptly.infrastructure.in.web.api.PromptsApi;
import com.promptly.infrastructure.in.web.dto.CreatePromptRequest;
import com.promptly.infrastructure.in.web.dto.GenerateFromIdeaRequest;
import com.promptly.infrastructure.in.web.dto.GenerateFromIdeaResponse;
import com.promptly.infrastructure.in.web.dto.PromptResponse;
import com.promptly.infrastructure.in.web.dto.PromptSummaryResponse;
import com.promptly.infrastructure.in.web.dto.UpdatePromptRequest;
import com.promptly.infrastructure.in.web.dto.VersionResponse;
import com.promptly.prompt.application.port.in.*;
import com.promptly.prompt.domain.model.Prompt;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
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
            @Nullable String projectId, Integer page, Integer size, @Nullable String sort,
            ServerWebExchange exchange) {
        var pageable = buildPageRequest(page, size, sort);
        Flux<Prompt> prompts = (projectId != null)
                ? getPromptUseCase.getPromptsByProjectId(projectId, pageable)
                : getPromptUseCase.getAllPrompts(pageable);
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
        var command = new RollbackPromptUseCase.RollbackPromptCommand(id, targetVersion, author);
        return rollbackPromptUseCase.rollbackToVersion(command)
                .map(mapper::toPromptResponse)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Void>> deletePrompt(
            String id, ServerWebExchange exchange) {
        return deletePromptUseCase.deletePrompt(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }

    @Override
    public Mono<ResponseEntity<GenerateFromIdeaResponse>> generateFromIdea(
            Mono<GenerateFromIdeaRequest> generateFromIdeaRequest, ServerWebExchange exchange) {
        // TODO: Implement AI-powered prompt generation from an idea
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build());
    }

    /**
     * Builds a PageRequest from query params. Parses sort string like "createdAt,desc".
     */
    private PageRequest buildPageRequest(Integer page, Integer size, String sort) {
        int p = (page != null) ? page : 0;
        int s = (size != null) ? size : 20;
        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(",");
            String property = parts[0].trim();
            Sort.Direction direction = (parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim()))
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            return PageRequest.of(p, s, Sort.by(direction, property));
        }
        return PageRequest.of(p, s, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

}
