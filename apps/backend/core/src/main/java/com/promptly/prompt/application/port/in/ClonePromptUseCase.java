package com.promptly.prompt.application.port.in;

import com.promptly.prompt.domain.model.Prompt;
import reactor.core.publisher.Mono;

/**
 * Inbound port for cloning an existing prompt.
 * Cloning creates a new prompt in DEV with the content from the source prompt,
 * regardless of the source prompt's environment or status.
 */
public interface ClonePromptUseCase {

    Mono<Prompt> clonePrompt(String sourcePromptId, ClonePromptCommand command);

    record ClonePromptCommand(
            String name,
            String description,
            String projectId,
            String author
    ) {}
}
