package com.promptly.prompt.application.port.in;

import com.promptly.prompt.domain.model.Prompt;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Inbound port for retrieving prompts.
 */
public interface GetPromptUseCase {

    Mono<Prompt> getPromptById(String id);

    Flux<Prompt> getAllPrompts();

    Flux<Prompt> getPromptsByProjectId(String projectId);

}
