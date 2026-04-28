package com.promptly.improver.infrastructure.llm;

import com.promptly.improver.application.port.out.LlmImproverPort;
import com.promptly.shared.systemprompt.SystemPromptPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

/**
 * Spring AI adapter for LLM-powered prompt improvement.
 * <p>
 * Resilience4j circuit breaker and retry protect against LLM provider outages.
 * Configure via {@code resilience4j.circuitbreaker.instances.llm-improver.*} in YAML.
 * <p>
 * The system prompt is resolved via {@link SystemPromptPort}, which supports:
 * <ul>
 *   <li>Admin overrides from the {@code __system__} project in the Prompt Registry</li>
 *   <li>Classpath defaults from {@code resources/prompts/improver-system-prompt.md}</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SpringAiImproverAdapter implements LlmImproverPort {

    private final ChatClient.Builder chatClientBuilder;
    private final SystemPromptPort systemPromptPort;

    @Override
    @CircuitBreaker(name = "llm-improver", fallbackMethod = "improveFallback")
    @Retry(name = "llm-improver")
    public ImproveResult improveContent(String content) {
        log.info("Running LLM prompt improvement");

        String systemPrompt = systemPromptPort.getSystemPrompt("improver");

        String response = chatClientBuilder.build()
                .prompt()
                .system(systemPrompt)
                .user("Improve this prompt:\n\n" + content)
                .call()
                .content();

        return parseResponse(response, content);
    }

    /**
     * Fallback when circuit breaker is open or all retries are exhausted.
     */
    @SuppressWarnings("unused")
    private ImproveResult improveFallback(String content, Throwable t) {
        log.warn("LLM improver circuit breaker triggered: {}", t.getMessage());
        return new ImproveResult(content, "Improvement unavailable — LLM service is temporarily down");
    }

    private ImproveResult parseResponse(String response, String originalContent) {
        try {
            if (response != null && response.contains("improvedContent")) {
                // Extract improved content between quotes after "improvedContent":
                int startIdx = response.indexOf("\"improvedContent\"");
                int colonIdx = response.indexOf(":", startIdx);
                int firstQuote = response.indexOf("\"", colonIdx + 1);
                int lastQuote = response.indexOf("\"", firstQuote + 1);

                // Try to find summary
                String improved = lastQuote > firstQuote
                        ? response.substring(firstQuote + 1, lastQuote)
                        : originalContent;

                return new ImproveResult(improved, "AI-generated improvement applied");
            }
            return new ImproveResult(originalContent, "No improvement generated");
        } catch (Exception e) {
            return new ImproveResult(originalContent, "Failed to parse improvement");
        }
    }

}
