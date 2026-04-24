package com.promptly.improver.application.port.in;

import reactor.core.publisher.Mono;

/**
 * Inbound port for AI-assisted prompt improvement.
 */
public interface ImprovePromptUseCase {

    Mono<ImprovementResult> improvePrompt(String promptId);

    Mono<Void> applyImprovement(String promptId, String improvedContent, String author);

    record ImprovementResult(
            String promptId,
            String originalContent,
            String improvedContent,
            String summary
    ) {}

}
