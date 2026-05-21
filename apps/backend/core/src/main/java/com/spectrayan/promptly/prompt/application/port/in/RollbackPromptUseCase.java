package com.spectrayan.promptly.prompt.application.port.in;

import com.spectrayan.promptly.prompt.domain.model.Prompt;
import reactor.core.publisher.Mono;

/**
 * Inbound port for rolling back a prompt to a previous version.
 * Creates a new version with the content from the target version.
 */
public interface RollbackPromptUseCase {

    record RollbackPromptCommand(String promptId, int targetVersion, String author) {}

    Mono<Prompt> rollbackToVersion(RollbackPromptCommand command);

}
