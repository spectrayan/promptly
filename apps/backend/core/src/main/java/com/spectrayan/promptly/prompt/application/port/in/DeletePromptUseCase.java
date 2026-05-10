package com.spectrayan.promptly.prompt.application.port.in;

import reactor.core.publisher.Mono;

/**
 * Inbound port for deleting a prompt.
 */
public interface DeletePromptUseCase {

    Mono<Void> deletePrompt(String id);

}
