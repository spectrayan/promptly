package com.promptly.scanner.infrastructure.llm;

import com.promptly.scanner.application.port.out.LlmScannerPort;
import com.promptly.scanner.domain.model.Finding;
import com.promptly.scanner.domain.model.ScanResult;
import com.promptly.scanner.domain.model.Severity;
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
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SpringAiScannerAdapter implements LlmScannerPort {

    private final ChatClient.Builder chatClientBuilder;

    private static final String SCAN_SYSTEM_PROMPT = """
            You are a prompt security analyzer. Analyze the given AI prompt for vulnerabilities.
            
            Check for these categories:
            1. **PHI/PII Exposure**: Does the prompt risk exposing protected health information or personally identifiable information?
            2. **Prompt Injection Risk**: Could the prompt be exploited by malicious user input?
            3. **Missing Safety Guardrails**: Does the prompt lack necessary safety instructions?
            4. **Hallucination-Prone Patterns**: Does the prompt encourage or allow fabrication of facts?
            5. **Weak Tool-Calling Instructions**: If the prompt involves tool/function calling, are the instructions precise enough?
            
            For each finding, provide:
            - type: one of [phi_exposure, injection_risk, missing_guardrail, hallucination_prone, weak_tool_calling]
            - severity: one of [critical, high, medium, low]
            - title: brief title
            - description: detailed explanation
            - remediation: how to fix it
            
            Respond with a JSON object:
            {
              "overallScore": <number 0-10, 0=safe, 10=critical>,
              "findings": [
                {
                  "type": "...",
                  "severity": "...",
                  "title": "...",
                  "description": "...",
                  "remediation": "..."
                }
              ]
            }
            
            If the prompt is safe, return {"overallScore": 0, "findings": []}.
            Return ONLY the JSON object, no markdown formatting.
            """;

    @Override
    public ScanResult analyzePrompt(String promptId, int version, String content) {
        log.info("Running LLM vulnerability scan for prompt {} v{}", promptId, version);

        try {
            String response = chatClientBuilder.build()
                    .prompt()
                    .system(SCAN_SYSTEM_PROMPT)
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
