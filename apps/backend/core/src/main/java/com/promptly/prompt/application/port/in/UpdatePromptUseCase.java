package com.promptly.prompt.application.port.in;

import com.promptly.prompt.domain.model.Prompt;
import reactor.core.publisher.Mono;

/**
 * Inbound port for updating an existing prompt.
 * Each update creates a new version — content is never overwritten.
 */
public interface UpdatePromptUseCase {

    Mono<Prompt> updatePrompt(String id, UpdatePromptCommand command);

    record UpdatePromptCommand(
            String content,
            String changeMessage,
            String author
    ) {}

}
