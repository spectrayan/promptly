package com.promptly.improver.application.port.in;

import reactor.core.publisher.Mono;

/**
 * Inbound port for AI-assisted prompt improvement and generation.
 */
public interface ImprovePromptUseCase {

    Mono<ImprovementResult> improvePrompt(String promptId);

    Mono<Void> applyImprovement(String promptId, String improvedContent, String author);

    /**
     * Generates a complete prompt from a short idea description.
     * Does not require an existing prompt in the registry.
     */
    Mono<GenerateFromIdeaResult> generateFromIdea(String idea);

    record ImprovementResult(
            String promptId,
            String originalContent,
            String improvedContent,
            String summary
    ) {}

    record GenerateFromIdeaResult(
            String generatedContent,
            String title,
            String summary
    ) {}

}
