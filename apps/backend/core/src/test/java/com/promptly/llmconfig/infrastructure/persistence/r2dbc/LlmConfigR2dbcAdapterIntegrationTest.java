package com.promptly.llmconfig.infrastructure.persistence.r2dbc;

import com.promptly.AbstractR2dbcIntegrationTest;
import com.promptly.llmconfig.application.port.out.LlmConfigPersistencePort;
import com.promptly.llmconfig.domain.model.LlmConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for LlmConfig R2DBC adapter.
 * Verifies save, query by project+feature, and delete operations.
 */
@SpringBootTest
class LlmConfigR2dbcAdapterIntegrationTest extends AbstractR2dbcIntegrationTest {

    @Autowired
    private LlmConfigPersistencePort configPort;

    private LlmConfig buildConfig(String projectId, String feature) {
        return LlmConfig.builder()
                .id(UUID.randomUUID().toString())
                .projectId(projectId)
                .feature(feature)
                .provider("openai")
                .model("gpt-4o")
                .temperature(0.7)
                .maxTokens(2048)
                .baseUrl("https://api.openai.com")
                .updatedAt(Instant.now())
                .updatedBy("test-user")
                .build();
    }

    @Test
    void shouldSaveAndFindByProjectIdAndFeature() {
        var projectId = "llm-proj-" + UUID.randomUUID();
        var config = buildConfig(projectId, "scanner");

        StepVerifier.create(
                configPort.save(config)
                        .then(configPort.findByProjectIdAndFeature(projectId, "scanner"))
        )
        .assertNext(found -> {
            assertThat(found.getProvider()).isEqualTo("openai");
            assertThat(found.getModel()).isEqualTo("gpt-4o");
            assertThat(found.getTemperature()).isEqualTo(0.7);
            assertThat(found.getMaxTokens()).isEqualTo(2048);
        })
        .verifyComplete();
    }

    @Test
    void shouldFindAllByProjectId() {
        var projectId = "llm-multi-" + UUID.randomUUID();
        var global = buildConfig(projectId, "global");
        var scanner = buildConfig(projectId, "scanner");

        StepVerifier.create(
                configPort.save(global)
                        .then(configPort.save(scanner))
                        .thenMany(configPort.findByProjectId(projectId))
                        .collectList()
        )
        .assertNext(configs -> assertThat(configs).hasSize(2))
        .verifyComplete();
    }

    @Test
    void shouldDeleteByProjectIdAndFeature() {
        var projectId = "llm-del-" + UUID.randomUUID();
        var config = buildConfig(projectId, "embedding");

        StepVerifier.create(
                configPort.save(config)
                        .then(configPort.deleteByProjectIdAndFeature(projectId, "embedding"))
                        .then(configPort.findByProjectIdAndFeature(projectId, "embedding"))
        )
        .verifyComplete(); // empty = deleted
    }

    @Test
    void shouldReturnEmptyForNonExistentFeature() {
        StepVerifier.create(
                configPort.findByProjectIdAndFeature("nonexistent-proj", "nonexistent-feature")
        )
        .verifyComplete();
    }
}
