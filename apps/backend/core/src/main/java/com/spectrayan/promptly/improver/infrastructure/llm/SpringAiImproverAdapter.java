package com.spectrayan.promptly.improver.infrastructure.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spectrayan.promptly.improver.application.port.out.LlmImproverPort;
import com.spectrayan.promptly.shared.systemprompt.SystemPromptPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

/**
 * Spring AI adapter for LLM-powered prompt improvement and generation.
 * <p>
 * Uses non-blocking {@link ChatClient#stream()} for all LLM interactions,
 * collecting streamed chunks into a single response. This avoids the
 * socket read timeouts that plague synchronous REST calls with thinking models.
 * <p>
 * Resilience is handled via Reactor operators:
 * <ul>
 *   <li>{@code .timeout()} — 2-minute timeout per call</li>
 *   <li>{@code .retryWhen()} — exponential backoff on transient failures</li>
 *   <li>{@code .onErrorResume()} — graceful fallback on exhausted retries</li>
 * </ul>
 * <p>
 * The system prompt is resolved via {@link SystemPromptPort}, which supports:
 * <ul>
 *   <li>Admin overrides from the {@code __system__} project in the Prompt Registry</li>
 *   <li>Classpath defaults from {@code resources/prompts/}</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SpringAiImproverAdapter implements LlmImproverPort {

    private static final Duration LLM_TIMEOUT = Duration.ofMinutes(2);
    private static final int MAX_RETRIES = 2;

    private final ChatClient.Builder chatClientBuilder;
    private final SystemPromptPort systemPromptPort;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<ImproveResult> improveContent(String content) {
        log.info("Running LLM prompt improvement (streaming)");

        String systemPrompt = systemPromptPort.getSystemPrompt("improver");

        return chatClientBuilder.build()
                .prompt()
                .system(systemPrompt)
                .user("Improve this prompt:\n\n" + content)
                .stream()
                .content()
                .collectList()
                .map(chunks -> String.join("", chunks))
                .timeout(LLM_TIMEOUT)
                .doOnNext(response -> log.debug("LLM improve response length: {} chars", response.length()))
                .map(response -> parseImproveResponse(response, content))
                .retryWhen(Retry.backoff(MAX_RETRIES, Duration.ofSeconds(1))
                        .filter(this::isRetryable)
                        .doBeforeRetry(signal -> log.warn("Retrying LLM improve (attempt {}): {}",
                                signal.totalRetries() + 1, signal.failure().getMessage())))
                .onErrorResume(e -> {
                    log.warn("LLM improve failed after retries: {} — {}", e.getClass().getSimpleName(), e.getMessage());
                    return Mono.just(new ImproveResult(content, "Improvement unavailable — LLM service is temporarily down"));
                });
    }

    @Override
    public Mono<GenerateResult> generateContent(String idea) {
        log.info("Running LLM prompt generation from idea (streaming)");

        String systemPrompt = systemPromptPort.getSystemPrompt("generator");

        return chatClientBuilder.build()
                .prompt()
                .system(systemPrompt)
                .user("Generate a production-quality prompt for the following idea:\n\n" + idea)
                .stream()
                .content()
                .collectList()
                .map(chunks -> String.join("", chunks))
                .timeout(LLM_TIMEOUT)
                .doOnNext(response -> log.debug("LLM generate response length: {} chars", response.length()))
                .map(response -> parseGenerateResponse(response, idea))
                .retryWhen(Retry.backoff(MAX_RETRIES, Duration.ofSeconds(1))
                        .filter(this::isRetryable)
                        .doBeforeRetry(signal -> log.warn("Retrying LLM generate (attempt {}): {}",
                                signal.totalRetries() + 1, signal.failure().getMessage())))
                .onErrorResume(e -> {
                    log.warn("LLM generate failed after retries: {} — {}", e.getClass().getSimpleName(), e.getMessage());
                    return Mono.just(new GenerateResult(idea, "Generated Prompt", "Generation unavailable — LLM service is temporarily down"));
                });
    }

    // ── Retry filter ────────────────────────────────────────────────

    private boolean isRetryable(Throwable t) {
        return t instanceof java.io.IOException
                || t instanceof java.net.SocketTimeoutException
                || t instanceof java.util.concurrent.TimeoutException;
    }

    // ── Response parsers ────────────────────────────────────────────

    /**
     * Lenient mapper for parsing unreliable LLM output.
     * <p>
     * LLMs frequently emit JSON with literal (unescaped) newlines inside string
     * values — especially when the content is multi-line markdown. Standard
     * Jackson rejects this as invalid JSON. This mapper tolerates it.
     * <p>
     * This is intentionally separate from the Spring-managed {@code ObjectMapper}
     * to avoid changing serialization behaviour for the rest of the application.
     */
    private static final ObjectMapper LENIENT_MAPPER = com.fasterxml.jackson.databind.json.JsonMapper.builder()
            .enable(com.fasterxml.jackson.core.json.JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS)
            .build();

    private ImproveResult parseImproveResponse(String response, String originalContent) {
        try {
            if (response == null || response.isBlank()) {
                return new ImproveResult(originalContent, "No improvement generated");
            }

            String jsonStr = stripMarkdownCodeFences(response);
            JsonNode root = parseJsonLenient(jsonStr);

            if (root != null && root.has("improvedContent")) {
                String improved = root.get("improvedContent").asText();
                String summary = root.has("summary")
                        ? root.get("summary").asText()
                        : "AI-generated improvement applied";
                return new ImproveResult(improved, summary);
            }

            // Fallback: try manual field extraction
            String improved = extractJsonStringField(jsonStr, "improvedContent");
            if (improved != null) {
                String summary = extractJsonStringField(jsonStr, "summary");
                return new ImproveResult(improved, summary != null ? summary : "AI-generated improvement applied");
            }

            log.warn("LLM improve response missing 'improvedContent' field, using raw response");
            return new ImproveResult(response, "AI-generated improvement applied");
        } catch (Exception e) {
            log.debug("Failed to parse improve response ({}), using raw response as content", e.getMessage());
            return new ImproveResult(response, "AI-generated improvement applied");
        }
    }

    private GenerateResult parseGenerateResponse(String response, String idea) {
        try {
            if (response == null || response.isBlank()) {
                return new GenerateResult(idea, "Generated Prompt", "Generation produced empty result");
            }

            String jsonStr = stripMarkdownCodeFences(response);
            JsonNode root = parseJsonLenient(jsonStr);

            if (root != null && root.has("generatedContent")) {
                String generated = root.get("generatedContent").asText();
                String title = root.has("title") ? root.get("title").asText() : "Generated Prompt";
                String summary = root.has("summary") ? root.get("summary").asText() : "AI-generated prompt from idea";
                return new GenerateResult(
                        generated != null && !generated.isBlank() ? generated : idea,
                        title,
                        summary
                );
            }

            // Fallback: try manual field extraction for malformed JSON
            String generated = extractJsonStringField(jsonStr, "generatedContent");
            if (generated != null) {
                String title = extractJsonStringField(jsonStr, "title");
                String summary = extractJsonStringField(jsonStr, "summary");
                return new GenerateResult(
                        generated,
                        title != null ? title : "Generated Prompt",
                        summary != null ? summary : "AI-generated prompt from idea"
                );
            }

            log.warn("LLM generate response missing 'generatedContent' field, using raw response");
            return new GenerateResult(response, "Generated Prompt", "AI-generated prompt from idea");
        } catch (Exception e) {
            log.debug("Failed to parse generate response ({}), using raw response as content", e.getMessage());
            return new GenerateResult(response, "Generated Prompt", "AI-generated prompt from idea");
        }
    }

    // ── JSON parsing helpers ────────────────────────────────────────

    /**
     * Multi-tier JSON parse: tries the Spring ObjectMapper first, then the
     * lenient mapper that tolerates unescaped control characters.
     *
     * @return parsed JsonNode, or {@code null} if both attempts fail
     */
    private JsonNode parseJsonLenient(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            log.debug("Standard JSON parse failed ({}), trying lenient parser", e.getMessage());
        }
        try {
            return LENIENT_MAPPER.readTree(json);
        } catch (Exception e) {
            log.debug("Lenient JSON parse also failed: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Manual extraction of a JSON string field value.
     * Handles escaped quotes within the value and unescapes JSON sequences.
     * Used as a last-resort fallback when Jackson cannot parse the LLM output.
     */
    private String extractJsonStringField(String json, String fieldName) {
        try {
            String key = "\"" + fieldName + "\"";
            int keyIdx = json.indexOf(key);
            if (keyIdx < 0) return null;
            int colonIdx = json.indexOf(":", keyIdx + key.length());
            if (colonIdx < 0) return null;

            int openQuote = json.indexOf("\"", colonIdx + 1);
            if (openQuote < 0) return null;

            // Find the closing quote (skip escaped quotes)
            int i = openQuote + 1;
            while (i < json.length()) {
                if (json.charAt(i) == '\\') {
                    i += 2; // skip escaped char
                } else if (json.charAt(i) == '"') {
                    break;
                } else {
                    i++;
                }
            }

            if (i >= json.length()) return null;
            String raw = json.substring(openQuote + 1, i);
            return unescapeJsonString(raw);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Unescapes JSON string escape sequences:
     * {@code \"} → ", {@code \n} → newline, {@code \t} → tab, {@code \\} → \
     */
    private String unescapeJsonString(String s) {
        if (s == null || s.isEmpty()) return s;
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '\\' && i + 1 < s.length()) {
                char next = s.charAt(i + 1);
                switch (next) {
                    case '"':  sb.append('"');  i++; break;
                    case '\\': sb.append('\\'); i++; break;
                    case '/':  sb.append('/');  i++; break;
                    case 'n':  sb.append('\n'); i++; break;
                    case 'r':  sb.append('\r'); i++; break;
                    case 't':  sb.append('\t'); i++; break;
                    default:   sb.append('\\'); break;
                }
            } else {
                sb.append(s.charAt(i));
            }
        }
        return sb.toString();
    }

    /**
     * Strip markdown code fences ({@code ```json ... ```}) from LLM responses.
     * Some models wrap JSON in fenced code blocks despite instructions not to.
     */
    private String stripMarkdownCodeFences(String text) {
        String trimmed = text.trim();
        if (trimmed.startsWith("```")) {
            // Remove opening fence (```json or ``` etc.)
            int firstNewline = trimmed.indexOf('\n');
            if (firstNewline > 0) {
                trimmed = trimmed.substring(firstNewline + 1);
            }
            // Remove closing fence
            if (trimmed.endsWith("```")) {
                trimmed = trimmed.substring(0, trimmed.length() - 3).trim();
            }
        }
        return trimmed;
    }

}

