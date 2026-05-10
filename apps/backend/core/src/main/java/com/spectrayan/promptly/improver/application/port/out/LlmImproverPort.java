package com.spectrayan.promptly.improver.application.port.out;

import reactor.core.publisher.Mono;

/**
 * Outbound port for LLM-based prompt improvement and generation.
 * <p>
 * Returns reactive {@link Mono} types for non-blocking integration
 * with the WebFlux pipeline. Implementations should use streaming
 * to avoid socket timeouts on long-running LLM calls.
 */
public interface LlmImproverPort {

    /**
     * Uses an LLM to improve the given prompt content.
     * Returns the improved version reactively.
     */
    Mono<ImproveResult> improveContent(String content);

    /**
     * Uses an LLM to generate a full prompt from a short idea description.
     * Unlike {@link #improveContent}, this creates content from scratch.
     */
    Mono<GenerateResult> generateContent(String idea);

    record ImproveResult(
            String improvedContent,
            String summary
    ) {}

    record GenerateResult(
            String generatedContent,
            String title,
            String summary
    ) {}

}
