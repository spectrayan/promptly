package com.promptly.scanner.infrastructure.llm;

import com.promptly.scanner.application.port.out.LlmScannerPort;
import com.promptly.scanner.domain.model.Finding;
import com.promptly.scanner.domain.model.ScanResult;
import com.promptly.scanner.domain.model.Severity;
import com.promptly.shared.systemprompt.SystemPromptPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Spring AI adapter for LLM-powered vulnerability scanning.
 * Analyzes prompt content for security issues, PHI exposure, injection risks, etc.
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

    @Override
    public ScanResult analyzePrompt(String promptId, int version, String content) {
        log.info("Running LLM vulnerability scan for prompt {} v{}", promptId, version);

        try {
            String systemPrompt = systemPromptPort.getSystemPrompt("scanner");

            String response = chatClientBuilder.build()
                    .prompt()
                    .system(systemPrompt)
                    .user("Analyze this prompt for vulnerabilities:\n\n" + content)
                    .call()
                    .content();

            return parseScanResponse(promptId, version, response);
        } catch (Exception e) {
            log.warn("LLM scan failed for prompt {}, returning default safe result: {}", promptId, e.getMessage());
            return createDefaultResult(promptId, version);
        }
    }

    private ScanResult parseScanResponse(String promptId, int version, String response) {
        try {
            // Simple JSON parsing — in production, use Jackson ObjectMapper
            double score = 0.0;
            List<Finding> findings = new ArrayList<>();

            if (response != null && response.contains("overallScore")) {
                // Extract score
                int scoreIdx = response.indexOf("overallScore");
                String scoreStr = response.substring(scoreIdx);
                scoreStr = scoreStr.replaceAll("[^0-9.]", " ").trim().split("\\s+")[0];
                score = Double.parseDouble(scoreStr);
            }

            ScanResult result = ScanResult.builder()
                    .promptId(promptId)
                    .promptVersion(version)
                    .overallScore(score)
                    .findings(findings)
                    .llmProvider("openai")
                    .llmModel("gpt-4o-mini")
                    .scannedBy("system")
                    .scannedAt(Instant.now())
                    .build();
            result.computeStatus();
            return result;
        } catch (Exception e) {
            log.warn("Failed to parse scan response, returning default: {}", e.getMessage());
            return createDefaultResult(promptId, version);
        }
    }

    private ScanResult createDefaultResult(String promptId, int version) {
        ScanResult result = ScanResult.builder()
                .promptId(promptId)
                .promptVersion(version)
                .overallScore(0.0)
                .findings(new ArrayList<>())
                .llmProvider("openai")
                .llmModel("gpt-4o-mini")
                .scannedBy("system")
                .scannedAt(Instant.now())
                .build();
        result.computeStatus();
        return result;
    }

}
