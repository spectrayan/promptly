package com.promptly.shared.systemprompt;

/**
 * Port for resolving system prompts used by AI-powered features
 * (scanner, improver, etc.).
 * <p>
 * Resolution precedence:
 * <ol>
 *   <li>Admin override from the Prompt Registry ({@code __system__} project)</li>
 *   <li>Classpath default from {@code src/main/resources/prompts/}</li>
 * </ol>
 * <p>
 * Implementations should cache resolved prompts and invalidate
 * on {@code PromptUpdated} events from the {@code __system__} project.
 */
public interface SystemPromptPort {

    /** Well-known project ID for system-level prompts. */
    String SYSTEM_PROJECT_ID = "__system__";

    /**
     * Returns the effective system prompt for the given feature.
     *
     * @param feature feature key (e.g. "scanner", "improver")
     * @return the resolved system prompt text, never null
     */
    String getSystemPrompt(String feature);

    /**
     * Returns the built-in default system prompt loaded from the classpath.
     * This is the fallback when no admin override exists.
     *
     * @param feature feature key
     * @return the default prompt text from classpath resources
     */
    String getDefaultPrompt(String feature);

    /**
     * Forces a cache refresh for the given feature.
     * Called when an admin resets a system prompt to defaults.
     *
     * @param feature feature key
     */
    void invalidateCache(String feature);
}
