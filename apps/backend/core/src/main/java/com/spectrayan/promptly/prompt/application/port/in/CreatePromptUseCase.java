package com.spectrayan.promptly.prompt.application.port.in;

import com.spectrayan.promptly.prompt.domain.model.Prompt;
import reactor.core.publisher.Mono;

/**
 * Inbound port for creating a new prompt.
 */
public interface CreatePromptUseCase {

    Mono<Prompt> createPrompt(CreatePromptCommand command);

    /**
     * Command object for prompt creation.
     */
    record CreatePromptCommand(
            String name,
            String description,
            String projectId,
            String contentFormat,
            String content,
            String author
    ) {}

}
