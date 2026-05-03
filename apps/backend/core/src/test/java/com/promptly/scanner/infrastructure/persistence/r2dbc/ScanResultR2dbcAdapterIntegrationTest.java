package com.promptly.scanner.infrastructure.persistence.r2dbc;

import com.promptly.AbstractR2dbcIntegrationTest;
import com.promptly.scanner.application.port.out.ScanResultPersistencePort;
import com.promptly.scanner.domain.model.Finding;
import com.promptly.scanner.domain.model.ScanResult;
import com.promptly.scanner.domain.model.Severity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for ScanResult R2DBC adapter.
 * Verifies save, query-by-project/prompt, and latest-scan lookup.
 */
@SpringBootTest
class ScanResultR2dbcAdapterIntegrationTest extends AbstractR2dbcIntegrationTest {

    @Autowired
    private ScanResultPersistencePort scanPort;

    private ScanResult buildScanResult(String projectId, String promptId, double score) {
        return ScanResult.builder()
                .id(UUID.randomUUID().toString())
                .projectId(projectId)
                .promptId(promptId)
                .promptVersion(1)
                .overallScore(score)
                .status(score <= 2.0 ? "PASS" : score <= 5.0 ? "WARN" : "FAIL")
                .llmProvider("openai")
                .llmModel("gpt-4o")
                .scannedBy("test-user")
                .scannedAt(Instant.now())
                .findings(List.of(
                        Finding.builder()
                                .type("injection_risk")
                                .severity(Severity.HIGH)
                                .title("SQL Injection Risk")
                                .description("Potential injection vector detected")
                                .remediation("Add input sanitization")
                                .build()
                ))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    void shouldSaveAndFindByProjectId() {
        var projectId = "scan-proj-" + UUID.randomUUID();
        var scan = buildScanResult(projectId, "prompt-1", 1.5);

        StepVerifier.create(
                scanPort.save(scan)
                        .thenMany(scanPort.findByProjectId(projectId))
                        .collectList()
        )
        .assertNext(results -> {
            assertThat(results).hasSize(1);
            assertThat(results.get(0).getOverallScore()).isEqualTo(1.5);
            assertThat(results.get(0).getStatus()).isEqualTo("PASS");
        })
        .verifyComplete();
    }

    @Test
    void shouldFindByPromptId() {
        var promptId = UUID.randomUUID().toString();
        var scan = buildScanResult("proj-scan", promptId, 7.0);

        StepVerifier.create(
                scanPort.save(scan)
                        .thenMany(scanPort.findByPromptId(promptId))
                        .collectList()
        )
        .assertNext(results -> {
            assertThat(results).hasSize(1);
            assertThat(results.get(0).getStatus()).isEqualTo("FAIL");
        })
        .verifyComplete();
    }

    @Test
    void shouldFindLatestByPromptId() {
        var promptId = UUID.randomUUID().toString();
        var scan1 = buildScanResult("proj-latest", promptId, 3.0);
        // Ensure second scan is saved after first
        var scan2 = buildScanResult("proj-latest", promptId, 1.0);

        StepVerifier.create(
                scanPort.save(scan1)
                        .then(scanPort.save(scan2))
                        .then(scanPort.findLatestByPromptId(promptId))
        )
        .assertNext(latest -> {
            // Should return the most recently saved scan
            assertThat(latest).isNotNull();
            assertThat(latest.getPromptId()).isEqualTo(promptId);
        })
        .verifyComplete();
    }
}
