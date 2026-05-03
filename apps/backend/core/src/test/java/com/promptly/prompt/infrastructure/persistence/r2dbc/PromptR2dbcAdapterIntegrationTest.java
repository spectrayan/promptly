package com.promptly.prompt.infrastructure.persistence.r2dbc;

import com.promptly.AbstractR2dbcIntegrationTest;
import com.promptly.prompt.application.port.out.PromptHistoryPersistencePort;
import com.promptly.prompt.application.port.out.PromptPersistencePort;
import com.promptly.prompt.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the R2DBC Prompt and PromptHistory adapters.
 * Verifies full CRUD lifecycle against a real PostgreSQL Testcontainer.
 */
@SpringBootTest
class PromptR2dbcAdapterIntegrationTest extends AbstractR2dbcIntegrationTest {

    @Autowired
    private PromptPersistencePort promptPort;

    @Autowired
    private PromptHistoryPersistencePort historyPort;

    @BeforeEach
    void cleanUp() {
        // Delete all history first (FK constraint), then prompts
        historyPort.findByPromptId("").subscribe(); // warm up
        promptPort.findAll()
                .flatMap(p -> historyPort.deleteByPromptId(p.getId()).then(promptPort.deleteById(p.getId())))
                .blockLast();
    }

    // ── Helper ───────────────────────────────────────────────────────

    private Prompt buildPrompt(String name, String projectId) {
        return Prompt.builder()
                .id(UUID.randomUUID().toString())
                .name(name)
                .description("Test description for " + name)
                .projectId(projectId)
                .contentFormat(ContentFormat.TEXT)
                .tags(Set.of("test", "integration"))
                .metadata(PromptMetadata.builder()
                        .model("gpt-4o")
                        .temperature(0.7)
                        .maxTokens(1024)
                        .systemContext("You are a helpful assistant")
                        .build())
                .currentVersion(0)
                .status(PromptStatus.DRAFT)
                .version(0L)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .createdBy("test-user")
                .updatedBy("test-user")
                .build();
    }

    private PromptVersion buildVersion(int versionNumber, String content) {
        return PromptVersion.builder()
                .versionNumber(versionNumber)
                .content(content)
                .changeMessage("Version " + versionNumber)
                .createdBy("test-user")
                .createdAt(Instant.now())
                .build();
    }

    // ── Prompt CRUD ──────────────────────────────────────────────────

    @Test
    void shouldSaveAndFindPromptById() {
        var prompt = buildPrompt("Save Test", "project-1");

        StepVerifier.create(
                promptPort.save(prompt)
                        .flatMap(saved -> promptPort.findById(saved.getId()))
        )
        .assertNext(found -> {
            assertThat(found.getName()).isEqualTo("Save Test");
            assertThat(found.getProjectId()).isEqualTo("project-1");
            assertThat(found.getContentFormat()).isEqualTo(ContentFormat.TEXT);
            assertThat(found.getTags()).containsExactlyInAnyOrder("test", "integration");
            assertThat(found.getMetadata().getModel()).isEqualTo("gpt-4o");
            assertThat(found.getMetadata().getTemperature()).isEqualTo(0.7);
            assertThat(found.getMetadata().getMaxTokens()).isEqualTo(1024);
            assertThat(found.getStatus()).isEqualTo(PromptStatus.DRAFT);
        })
        .verifyComplete();
    }

    @Test
    void shouldFindPromptsByProjectId() {
        var p1 = buildPrompt("Prompt A", "proj-alpha");
        var p2 = buildPrompt("Prompt B", "proj-alpha");
        var p3 = buildPrompt("Prompt C", "proj-beta");

        StepVerifier.create(
                promptPort.save(p1)
                        .then(promptPort.save(p2))
                        .then(promptPort.save(p3))
                        .thenMany(promptPort.findByProjectId("proj-alpha"))
                        .collectList()
        )
        .assertNext(prompts -> {
            assertThat(prompts).hasSize(2);
            assertThat(prompts).allMatch(p -> p.getProjectId().equals("proj-alpha"));
        })
        .verifyComplete();
    }

    @Test
    void shouldSupportPagination() {
        var p1 = buildPrompt("Page A", "paged-proj");
        var p2 = buildPrompt("Page B", "paged-proj");
        var p3 = buildPrompt("Page C", "paged-proj");

        StepVerifier.create(
                promptPort.save(p1)
                        .then(promptPort.save(p2))
                        .then(promptPort.save(p3))
                        .thenMany(promptPort.findByProjectId("paged-proj", PageRequest.of(0, 2)))
                        .collectList()
        )
        .assertNext(prompts -> assertThat(prompts).hasSize(2))
        .verifyComplete();
    }

    @Test
    void shouldDeletePromptById() {
        var prompt = buildPrompt("Delete Me", "del-proj");

        StepVerifier.create(
                promptPort.save(prompt)
                        .flatMap(saved -> promptPort.deleteById(saved.getId())
                                .then(promptPort.findById(saved.getId())))
        )
        .verifyComplete(); // empty = not found
    }

    @Test
    void shouldCheckExistsByNameAndProjectId() {
        var prompt = buildPrompt("Unique Name", "dup-proj");

        StepVerifier.create(
                promptPort.save(prompt)
                        .then(promptPort.existsByNameAndProjectId("Unique Name", "dup-proj"))
        )
        .assertNext(exists -> assertThat(exists).isTrue())
        .verifyComplete();

        StepVerifier.create(
                promptPort.existsByNameAndProjectId("Non-Existent", "dup-proj")
        )
        .assertNext(exists -> assertThat(exists).isFalse())
        .verifyComplete();
    }

    // ── Prompt History ───────────────────────────────────────────────

    @Test
    void shouldSaveAndRetrieveVersionHistory() {
        var prompt = buildPrompt("Versioned Prompt", "ver-proj");

        StepVerifier.create(
                promptPort.save(prompt)
                        .flatMap(saved -> {
                            var v1 = buildVersion(1, "Initial content");
                            var v2 = buildVersion(2, "Updated content");
                            return historyPort.save(saved.getId(), v1)
                                    .then(historyPort.save(saved.getId(), v2))
                                    .thenMany(historyPort.findByPromptId(saved.getId()))
                                    .collectList();
                        })
        )
        .assertNext(versions -> {
            assertThat(versions).hasSize(2);
            assertThat(versions.get(0).getVersionNumber()).isEqualTo(1);
            assertThat(versions.get(0).getContent()).isEqualTo("Initial content");
            assertThat(versions.get(1).getVersionNumber()).isEqualTo(2);
            assertThat(versions.get(1).getContent()).isEqualTo("Updated content");
        })
        .verifyComplete();
    }

    @Test
    void shouldFindSpecificVersionByNumber() {
        var prompt = buildPrompt("Specific Version", "sv-proj");

        StepVerifier.create(
                promptPort.save(prompt)
                        .flatMap(saved -> historyPort.save(saved.getId(), buildVersion(1, "V1"))
                                .then(historyPort.save(saved.getId(), buildVersion(2, "V2")))
                                .then(historyPort.findByPromptIdAndVersion(saved.getId(), 2)))
        )
        .assertNext(v -> {
            assertThat(v.getVersionNumber()).isEqualTo(2);
            assertThat(v.getContent()).isEqualTo("V2");
        })
        .verifyComplete();
    }

    @Test
    void shouldCascadeDeleteVersionsOnDeleteByPromptId() {
        var prompt = buildPrompt("Cascade Delete", "cd-proj");

        StepVerifier.create(
                promptPort.save(prompt)
                        .flatMap(saved -> historyPort.save(saved.getId(), buildVersion(1, "Content"))
                                .then(historyPort.deleteByPromptId(saved.getId()))
                                .thenMany(historyPort.findByPromptId(saved.getId()))
                                .collectList())
        )
        .assertNext(versions -> assertThat(versions).isEmpty())
        .verifyComplete();
    }
}
