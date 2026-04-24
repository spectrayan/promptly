package com.promptly.improver.infrastructure.llm;

import com.promptly.improver.application.port.out.LlmImproverPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

/**
 * Spring AI adapter for LLM-powered prompt improvement.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SpringAiImproverAdapter implements LlmImproverPort {

    private final ChatClient.Builder chatClientBuilder;

    private static final String IMPROVE_SYSTEM_PROMPT = """
            You are an expert prompt engineer. Your task is to improve the given AI prompt.
            
            Improvements should include:
            1. **Clarity**: Make instructions clearer and more precise
            2. **Safety**: Add safety guardrails if missing
            3. **Structure**: Improve the overall structure and organization
            4. **Examples**: Add examples if they would help (few-shot prompting)
            5. **Determinism**: Reduce ambiguity to improve consistency
            6. **Tool-calling**: Improve function/tool descriptions if present
            
            Respond with a JSON object:
            {
              "improvedContent": "the improved prompt text",
              "summary": "brief summary of changes made"
            }
            
            Return ONLY the JSON object, no markdown formatting.
            """;

    @Override
    public ImproveResult improveContent(String content) {
        log.info("Running LLM prompt improvement");

        try {
            String response = chatClientBuilder.build()
                    .prompt()
                    .system(IMPROVE_SYSTEM_PROMPT)
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
