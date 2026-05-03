package com.promptly.prompt;

import com.promptly.AbstractIntegrationTest;
import com.promptly.prompt.application.port.in.*;
import com.promptly.prompt.application.port.out.PromptHistoryPersistencePort;
import com.promptly.prompt.infrastructure.persistence.mongo.repository.PromptReactiveMongoRepository;
import com.promptly.prompt.infrastructure.persistence.mongo.repository.PromptHistoryReactiveMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.ApplicationModuleTest.BootstrapMode;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the Prompt Registry module.
 * Uses {@code @ApplicationModuleTest} with {@code ALL_DEPENDENCIES} to bootstrap
 * this module and its transitive dependencies (project, shared, infrastructure).
 * Verifies the full create→read→update→delete lifecycle against real MongoDB.
 */
@ApplicationModuleTest(mode = BootstrapMode.ALL_DEPENDENCIES)
class PromptModuleIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private PromptReactiveMongoRepository promptRepository;

    @Autowired
    private PromptHistoryReactiveMongoRepository historyRepository;

    @Autowired
    private PromptHistoryPersistencePort historyPort;

    @BeforeEach
    void setUp() {
        historyRepository.deleteAll().block();
        promptRepository.deleteAll().block();
    }

    @Autowired
    private CreatePromptUseCase createPromptUseCase;

    @Autowired
    private GetPromptUseCase getPromptUseCase;

    @Autowired
    private UpdatePromptUseCase updatePromptUseCase;

    @Autowired
    private DeletePromptUseCase deletePromptUseCase;

    @Test
    void shouldCreateAndRetrievePrompt() {
        var command = new CreatePromptUseCase.CreatePromptCommand(
                "Integration Test Prompt", "A test prompt",
                "test-project", "TEXT", "Hello, this is a test prompt.", "test-user"
        );

        StepVerifier.create(
                createPromptUseCase.createPrompt(command)
                        .flatMap(prompt -> getPromptUseCase.getPromptById(prompt.getId()))
        )
        .assertNext(prompt -> {
            assertThat(prompt.getName()).isEqualTo("Integration Test Prompt");
            assertThat(prompt.getCurrentVersion()).isEqualTo(1);
        })
        .verifyComplete();

        // Verify history was persisted separately
        StepVerifier.create(
                promptRepository.findAll().single()
                        .flatMapMany(doc -> historyPort.findByPromptId(doc.getId()))
                        .collectList()
        )
        .assertNext(versions -> {
            assertThat(versions).hasSize(1);
            assertThat(versions.get(0).getContent()).isEqualTo("Hello, this is a test prompt.");
            assertThat(versions.get(0).getVersionNumber()).isEqualTo(1);
        })
        .verifyComplete();
    }

    @Test
    void shouldUpdatePromptAndCreateNewVersion() {
        var createCmd = new CreatePromptUseCase.CreatePromptCommand(
                "Update Test", "Will be updated",
                "test-project", "TEXT", "Original content", "test-user"
        );

        var updateCmd = new UpdatePromptUseCase.UpdatePromptCommand(
                "Updated content", "Updated in test", "test-user"
        );

        StepVerifier.create(
                createPromptUseCase.createPrompt(createCmd)
                        .flatMap(prompt -> updatePromptUseCase.updatePrompt(prompt.getId(), updateCmd))
        )
        .assertNext(prompt -> {
            assertThat(prompt.getCurrentVersion()).isEqualTo(2);
        })
        .verifyComplete();

        // Verify both versions exist in history
        StepVerifier.create(
                promptRepository.findAll().single()
                        .flatMapMany(doc -> historyPort.findByPromptId(doc.getId()))
                        .collectList()
        )
        .assertNext(versions -> assertThat(versions).hasSize(2))
        .verifyComplete();
    }

    @Test
    void shouldDeletePrompt() {
        var command = new CreatePromptUseCase.CreatePromptCommand(
                "Delete Test", "Will be deleted",
                "test-project", "TEXT", "To be deleted", "test-user"
        );

        StepVerifier.create(
                createPromptUseCase.createPrompt(command)
                        .flatMap(prompt -> deletePromptUseCase.deletePrompt(prompt.getId())
                                .then(getPromptUseCase.getPromptById(prompt.getId())))
        )
        .expectError()
        .verify();
    }

    @Test
    void shouldListPromptsByProjectId() {
        var cmd1 = new CreatePromptUseCase.CreatePromptCommand(
                "List Test A", null, "project-alpha", "TEXT", "Content A", "test-user"
        );
        var cmd2 = new CreatePromptUseCase.CreatePromptCommand(
                "List Test B", null, "project-alpha", "TEXT", "Content B", "test-user"
        );

        StepVerifier.create(
                createPromptUseCase.createPrompt(cmd1)
                        .then(createPromptUseCase.createPrompt(cmd2))
                        .thenMany(getPromptUseCase.getPromptsByProjectId("project-alpha"))
                        .collectList()
        )
        .assertNext(prompts -> assertThat(prompts).hasSizeGreaterThanOrEqualTo(2))
        .verifyComplete();
    }

}
