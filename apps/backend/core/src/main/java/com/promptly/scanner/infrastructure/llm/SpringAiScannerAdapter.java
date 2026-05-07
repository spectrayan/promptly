package com.promptly.scanner.infrastructure.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.promptly.scanner.application.port.out.LlmScannerPort;
import com.promptly.scanner.domain.model.Finding;
import com.promptly.scanner.domain.model.ScanResult;
import com.promptly.scanner.domain.model.Severity;
import com.promptly.shared.systemprompt.SystemPromptPort;
import com.promptly.shared.config.PromptlyProperties;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatOptions;
import org.springframework.ai.vertexai.gemini.common.VertexAiGeminiSafetySetting;
import org.springframework.ai.vertexai.gemini.common.VertexAiGeminiSafetySetting.HarmBlockThreshold;
import org.springframework.ai.vertexai.gemini.common.VertexAiGeminiSafetySetting.HarmCategory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Spring AI adapter for LLM-powered vulnerability scanning.
 * Analyzes prompt content for security issues, PHI exposure, injection risks, etc.
 * <p>
 * Resilience4j circuit breaker and retry protect against LLM provider outages.
 * Configure via {@code resilience4j.circuitbreaker.instances.llm-scanner.*} in YAML.
 * <p>
 * The system prompt is resolved via {@link SystemPromptPort}, which supports:
 * <ul>
 *   <li>Admin overrides from the {@code __system__} project in the Prompt Registry</li>
 *   <li>Classpath defaults from {@code resources/prompts/scanner-system-prompt.md}</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SpringAiScannerAdapter implements LlmScannerPort {

    private final ChatClient.Builder chatClientBuilder;
    private final SystemPromptPort systemPromptPort;
    private final ObjectMapper objectMapper;
    private final PromptlyProperties properties;

    @Override
    @CircuitBreaker(name = "llm-scanner", fallbackMethod = "scanFallback")
    @Retry(name = "llm-scanner")
    public ScanResult analyzePrompt(String promptId, int version, String content) {
        log.info("Running LLM vulnerability scan for prompt {} v{}", promptId, version);

        String systemPrompt = systemPromptPort.getSystemPrompt("scanner");

        // Disable safety filters — the scanner intentionally analyzes dangerous content
        var safetySettings = java.util.List.of(
                VertexAiGeminiSafetySetting.builder()
                        .withCategory(HarmCategory.HARM_CATEGORY_HARASSMENT)
                        .withThreshold(HarmBlockThreshold.BLOCK_NONE).build(),
                VertexAiGeminiSafetySetting.builder()
                        .withCategory(HarmCategory.HARM_CATEGORY_HATE_SPEECH)
                        .withThreshold(HarmBlockThreshold.BLOCK_NONE).build(),
                VertexAiGeminiSafetySetting.builder()
                        .withCategory(HarmCategory.HARM_CATEGORY_SEXUALLY_EXPLICIT)
                        .withThreshold(HarmBlockThreshold.BLOCK_NONE).build(),
                VertexAiGeminiSafetySetting.builder()
                        .withCategory(HarmCategory.HARM_CATEGORY_DANGEROUS_CONTENT)
                        .withThreshold(HarmBlockThreshold.BLOCK_NONE).build()
        );

        var chatOptions = VertexAiGeminiChatOptions.builder()
                .safetySettings(safetySettings)
                .build();

        String response = chatClientBuilder.build()
                .prompt()
                .system(systemPrompt)
                .user("Analyze this prompt for vulnerabilities:\n\n" + content)
                .options(chatOptions)
                .call()
                .content();

        log.debug("LLM scan response for prompt {} v{}: {}", promptId, version, response);
        return parseScanResponse(promptId, version, response);
    }

    /**
     * Fallback when circuit breaker is open or all retries are exhausted.
     * Returns a result with status=ERROR so the UI can distinguish between
     * "scanned and clean" vs "scan infrastructure failure".
     */
    @SuppressWarnings("unused")
    private ScanResult scanFallback(String promptId, int version, String content, Throwable t) {
        log.error("LLM scan failed for prompt {} v{}: {}", promptId, version, t.getMessage(), t);
        ScanResult result = ScanResult.builder()
                .promptId(promptId)
                .promptVersion(version)
                .overallScore(0.0)
                .findings(new ArrayList<>())
                .llmProvider(resolveProvider())
                .llmModel(resolveModel())
                .scannedBy("system")
                .scannedAt(Instant.now())
                .status("ERROR")
                .build();
        return result;
    }

    /**
     * Lenient mapper for parsing unreliable LLM output.
     * LLMs frequently emit JSON with literal (unescaped) newlines inside string values.
     * Separate from the Spring-managed ObjectMapper to avoid global side-effects.
     */
    private static final ObjectMapper LENIENT_MAPPER = com.fasterxml.jackson.databind.json.JsonMapper.builder()
            .enable(com.fasterxml.jackson.core.json.JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS)
            .build();

    private ScanResult parseScanResponse(String promptId, int version, String response) {
        try {
            double score = 0.0;
            List<Finding> findings = new ArrayList<>();

            if (response == null || response.isBlank()) {
                log.warn("LLM returned empty scan response for prompt {} v{}", promptId, version);
                return createDefaultResult(promptId, version);
            }

            // Strip markdown code fences if the LLM wrapped the JSON in ```json ... ```
            String jsonStr = stripMarkdownCodeFences(response);

            JsonNode root = parseJsonLenient(jsonStr);
            if (root == null) {
                log.warn("Failed to parse scan JSON for prompt {} v{}", promptId, version);
                log.debug("Raw LLM response: {}", response);
                return createDefaultResult(promptId, version);
            }

            // Extract overallScore
            if (root.has("overallScore")) {
                score = root.get("overallScore").asDouble(0.0);
            }

            // Extract findings array
            if (root.has("findings") && root.get("findings").isArray()) {
                for (JsonNode node : root.get("findings")) {
                    Finding finding = Finding.builder()
                            .type(getTextOrNull(node, "type"))
                            .severity(parseSeverity(getTextOrNull(node, "severity")))
                            .title(getTextOrNull(node, "title"))
                            .description(getTextOrNull(node, "description"))
                            .remediation(getTextOrNull(node, "remediation"))
                            .build();
                    findings.add(finding);
                }
                log.info("Parsed {} findings from LLM scan response for prompt {} v{}",
                        findings.size(), promptId, version);
            }

            ScanResult result = ScanResult.builder()
                    .promptId(promptId)
                    .promptVersion(version)
                    .overallScore(score)
                    .findings(findings)
                    .llmProvider(resolveProvider())
                    .llmModel(resolveModel())
                    .scannedBy("system")
                    .scannedAt(Instant.now())
                    .build();
            result.computeStatus();
            return result;
        } catch (Exception e) {
            log.warn("Failed to parse scan response for prompt {} v{}: {}",
                    promptId, version, e.getMessage());
            log.debug("Raw LLM response: {}", response);
            return createDefaultResult(promptId, version);
        }
    }

    /**
     * Multi-tier JSON parse: tries the Spring ObjectMapper first, then the
     * lenient mapper that tolerates unescaped control characters.
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
     * Strip markdown code fences (```json ... ```) from LLM responses.
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

    private String getTextOrNull(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull()
                ? node.get(field).asText()
                : null;
    }

    private Severity parseSeverity(String value) {
        if (value == null) return null;
        try {
            return Severity.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.debug("Unknown severity value '{}', defaulting to MEDIUM", value);
            return Severity.MEDIUM;
        }
    }

    private ScanResult createDefaultResult(String promptId, int version) {
        ScanResult result = ScanResult.builder()
                .promptId(promptId)
                .promptVersion(version)
                .overallScore(0.0)
                .findings(new ArrayList<>())
                .llmProvider(resolveProvider())
                .llmModel(resolveModel())
                .scannedBy("system")
                .scannedAt(Instant.now())
                .build();
        result.computeStatus();
        return result;
    }

    // ── Config resolution (scanner override → global default) ────

    private String resolveProvider() {
        var scanner = properties.getLlm().getScanner();
        return (scanner != null && scanner.getProvider() != null)
                ? scanner.getProvider()
                : properties.getLlm().getProvider();
    }

    private String resolveModel() {
        var scanner = properties.getLlm().getScanner();
        return (scanner != null && scanner.getModel() != null)
                ? scanner.getModel()
                : properties.getLlm().getModel();
    }
}
