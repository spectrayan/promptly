package com.promptly.improver.application.port.out;

/**
 * Outbound port for LLM-based prompt improvement.
 */
public interface LlmImproverPort {

    /**
     * Uses an LLM to improve the given prompt content.
     * Returns the improved version.
     */
    ImproveResult improveContent(String content);

    record ImproveResult(
            String improvedContent,
            String summary
    ) {}

}
