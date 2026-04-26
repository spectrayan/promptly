package com.promptly.prompt.application.service;

import com.promptly.prompt.PromptRolledBack;
import com.promptly.prompt.application.port.in.RollbackPromptUseCase.RollbackPromptCommand;
import com.promptly.prompt.application.port.out.PromptPersistencePort;
import com.promptly.prompt.domain.model.*;
import com.promptly.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link RollbackPromptService}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RollbackPromptService")
class RollbackPromptServiceTest {

    @Mock private PromptPersistencePort persistencePort;
    @Mock private ApplicationEventPublisher eventPublisher;

    private RollbackPromptService service;

    @BeforeEach
    void setUp() {
        service = new RollbackPromptService(persistencePort, eventPublisher);
    }

    private Prompt twoVersionDraft() {
        Prompt p = Prompt.builder()
                .id("p-1").name("Test").description("d").projectId("proj-1")
                .contentFormat(ContentFormat.TEXT).tags(Set.of())
                .status(PromptStatus.DRAFT).currentVersion(0)
                .versions(new ArrayList<>()).build();
        p.createNewVersion("Version 1 content", "Initial", "alice");
        p.createNewVersion("Version 2 content", "Update", "bob");
        return p;
    }

    // ═══════════════════════════════════════════════════════════════
    // Success
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("success path")
    class SuccessTests {

        @BeforeEach
        void stub() {
            when(persistencePort.findById("p-1")).thenReturn(Mono.just(twoVersionDraft()));
            when(persistencePort.save(any(Prompt.class)))
                    .thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        }

        @Test
        @DisplayName("should create new version with content from target version")
        void shouldRollback() {
            var command = new RollbackPromptCommand("p-1", 1, "carol");

            StepVerifier.create(service.rollbackToVersion(command))
                    .assertNext(prompt -> {
                        // Version 3 should have content from version 1
                        assertThat(prompt.getCurrentVersion()).isEqualTo(3);
                        PromptVersion v3 = prompt.getVersion(3);
                        assertThat(v3.getContent()).isEqualTo("Version 1 content");
                        assertThat(v3.getChangeMessage()).contains("Rollback to version 1");
                        assertThat(v3.getCreatedBy()).isEqualTo("carol");
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("should publish PromptRolledBack event")
        void shouldPublishEvent() {
            service.rollbackToVersion(new RollbackPromptCommand("p-1", 1, "carol")).block();

            ArgumentCaptor<PromptRolledBack> captor = ArgumentCaptor.forClass(PromptRolledBack.class);
            verify(eventPublisher).publishEvent(captor.capture());

            PromptRolledBack event = captor.getValue();
            assertThat(event.aggregateId()).isEqualTo("p-1");
            assertThat(event.fromVersion()).isEqualTo(2);
            assertThat(event.toVersion()).isEqualTo(1);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // Error paths
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("error paths")
    class ErrorTests {

        @Test
        @DisplayName("should fail when prompt not found")
        void promptNotFound() {
            when(persistencePort.findById("p-999")).thenReturn(Mono.empty());

            StepVerifier.create(service.rollbackToVersion(
                    new RollbackPromptCommand("p-999", 1, "alice")
            ))
                    .expectError(ResourceNotFoundException.class)
                    .verify();

            verify(persistencePort, never()).save(any());
        }

        @Test
        @DisplayName("should fail when target version does not exist")
        void targetVersionNotFound() {
            when(persistencePort.findById("p-1")).thenReturn(Mono.just(twoVersionDraft()));

            StepVerifier.create(service.rollbackToVersion(
                    new RollbackPromptCommand("p-1", 99, "alice")
            ))
                    .expectError(IllegalArgumentException.class)
                    .verify();
        }

        @Test
        @DisplayName("should fail when prompt has only one version")
        void singleVersionNotRollbackable() {
            Prompt p = Prompt.builder()
                    .id("p-1").name("Test").description("d").projectId("proj-1")
                    .contentFormat(ContentFormat.TEXT).tags(Set.of())
                    .status(PromptStatus.DRAFT).currentVersion(0)
                    .versions(new ArrayList<>()).build();
            p.createNewVersion("only version", "init", "alice");

            when(persistencePort.findById("p-1")).thenReturn(Mono.just(p));

            StepVerifier.create(service.rollbackToVersion(
                    new RollbackPromptCommand("p-1", 1, "alice")
            ))
                    .expectError(IllegalStateException.class)
                    .verify();
        }
    }
}
