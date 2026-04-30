package com.promptly.prompt.application.service;

import com.promptly.shared.domain.event.PromptCreated;
import com.promptly.prompt.application.port.in.CreatePromptUseCase.CreatePromptCommand;
import com.promptly.prompt.application.port.out.PromptPersistencePort;
import com.promptly.prompt.domain.model.Prompt;
import com.promptly.prompt.domain.model.PromptStatus;
import com.promptly.shared.exception.DuplicateResourceException;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link CreatePromptService}.
 * Uses Mockito mocks for the persistence port and event publisher.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CreatePromptService")
class CreatePromptServiceTest {

    @Mock private PromptPersistencePort persistencePort;
    @Mock private ApplicationEventPublisher eventPublisher;

    private CreatePromptService service;

    @BeforeEach
    void setUp() {
        service = new CreatePromptService(persistencePort, eventPublisher);
    }

    private CreatePromptCommand command() {
        return new CreatePromptCommand(
                "Login Helper", "A login prompt", "proj-1", "TEXT", "Hello {{user}}", "alice"
        );
    }

    // ═══════════════════════════════════════════════════════════════
    // Success path
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("success path")
    class SuccessTests {

        @BeforeEach
        void stubSuccess() {
            when(persistencePort.existsByNameAndProjectId("Login Helper", "proj-1"))
                    .thenReturn(Mono.just(false));
            when(persistencePort.save(any(Prompt.class)))
                    .thenAnswer(inv -> {
                        Prompt p = inv.getArgument(0);
                        p.setId("p-generated");
                        return Mono.just(p);
                    });
        }

        @Test
        @DisplayName("should create prompt with DRAFT status and version 1")
        void shouldCreateDraftWithVersion1() {
            StepVerifier.create(service.createPrompt(command()))
                    .assertNext(prompt -> {
                        assertThat(prompt.getStatus()).isEqualTo(PromptStatus.DRAFT);
                        assertThat(prompt.getCurrentVersion()).isEqualTo(1);
                        assertThat(prompt.getName()).isEqualTo("Login Helper");
                        assertThat(prompt.getProjectId()).isEqualTo("proj-1");
                        assertThat(prompt.getVersions()).hasSize(1);
                        assertThat(prompt.getVersions().getFirst().getContent()).isEqualTo("Hello {{user}}");
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("should publish PromptCreated event")
        void shouldPublishEvent() {
            service.createPrompt(command()).block();

            ArgumentCaptor<PromptCreated> captor = ArgumentCaptor.forClass(PromptCreated.class);
            verify(eventPublisher).publishEvent(captor.capture());

            PromptCreated event = captor.getValue();
            assertThat(event.promptId()).isEqualTo("p-generated");
            assertThat(event.name()).isEqualTo("Login Helper");
            assertThat(event.projectId()).isEqualTo("proj-1");
            assertThat(event.version()).isEqualTo(1);
        }

        @Test
        @DisplayName("should save to persistence port")
        void shouldSave() {
            service.createPrompt(command()).block();

            verify(persistencePort).save(argThat(p ->
                    "Login Helper".equals(p.getName()) && p.getStatus() == PromptStatus.DRAFT
            ));
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // Duplicate name
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("duplicate name")
    class DuplicateTests {

        @Test
        @DisplayName("should reject duplicate name within same project")
        void shouldRejectDuplicate() {
            when(persistencePort.existsByNameAndProjectId("Login Helper", "proj-1"))
                    .thenReturn(Mono.just(true));

            StepVerifier.create(service.createPrompt(command()))
                    .expectError(DuplicateResourceException.class)
                    .verify();

            verify(persistencePort, never()).save(any());
            verifyNoInteractions(eventPublisher);
        }
    }
}
