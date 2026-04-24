package com.promptly.prompt.application.port.in;

import com.promptly.prompt.domain.model.Prompt;
import reactor.core.publisher.Mono;

/**
 * Inbound port for rolling back a prompt to a previous version.
 * Creates a new version with the content from the target version.
 */
public interface RollbackPromptUseCase {

    Mono<Prompt> rollbackToVersion(String id, int targetVersion, String author);

}
