package com.spectrayan.promptly.prompt;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Public API of the Prompt module, exposed to other modules.
 * This is the ONLY interface other modules should use to interact with prompts.
 * <p>
 * Returns {@link PromptProjection} DTOs instead of the domain aggregate,
 * ensuring that consumers cannot mutate internal domain state.
 * Internal implementation details (repositories, persistence) remain hidden
 * behind Spring Modulith's module boundary.
 */
public interface PromptModuleApi {

    Mono<PromptProjection> findById(String id);

    Flux<PromptProjection> findByProjectId(String projectId);

    Flux<PromptProjection> findAll();

    Mono<PromptProjection> updatePrompt(String id, String content, String changeMessage, String author);

}
