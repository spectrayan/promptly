package com.promptly.prompt;

import com.promptly.prompt.domain.model.Prompt;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Public API of the Prompt module, exposed to other modules.
 * This is the ONLY interface other modules should use to interact with prompts.
 * <p>
 * Internal implementation details (repositories, persistence) remain hidden
 * behind Spring Modulith's module boundary.
 */
public interface PromptModuleApi {

    Mono<Prompt> findById(String id);

    Flux<Prompt> findByProjectId(String projectId);

    Flux<Prompt> findAll();

    Mono<Prompt> updatePrompt(String id, String content, String changeMessage, String author);

}
