package com.promptly.improver.infrastructure.llm;

import com.promptly.improver.application.port.out.LlmImproverPort;
import com.promptly.shared.systemprompt.SystemPromptPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

/**
 * Spring AI adapter for LLM-powered prompt improvement.
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
    public ImproveResult improveContent(String content) {
        log.info("Running LLM prompt improvement");

        try {
            String systemPrompt = systemPromptPort.getSystemPrompt("improver");

            String response = chatClientBuilder.build()
                    .prompt()
                    .system(systemPrompt)
                    .user("Improve this prompt:\n\n" + content)
                    .call()
                    .content();

            return parseResponse(response, content);
        } catch (Exception e) {
            log.warn("LLM improvement failed: {}", e.getMessage());
            return new ImproveResult(content, "Improvement unavailable — LLM call failed");
        }
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
