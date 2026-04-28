package com.promptly.prompt.application.port.in;

import com.promptly.prompt.domain.model.Prompt;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Inbound port for retrieving prompts.
 */
public interface GetPromptUseCase {

    Mono<Prompt> getPromptById(String id);

    Flux<Prompt> getAllPrompts();

    Flux<Prompt> getAllPrompts(Pageable pageable);

    Flux<Prompt> getPromptsByProjectId(String projectId);

    Flux<Prompt> getPromptsByProjectId(String projectId, Pageable pageable);

}
