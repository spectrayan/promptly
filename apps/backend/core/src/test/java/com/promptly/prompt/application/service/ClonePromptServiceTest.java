package com.promptly.prompt.application.service;

import com.promptly.shared.domain.event.PromptCreated;
import com.promptly.prompt.application.port.in.ClonePromptUseCase.ClonePromptCommand;
import com.promptly.prompt.application.port.out.PromptHistoryPersistencePort;
import com.promptly.prompt.application.port.out.PromptPersistencePort;
import com.promptly.prompt.domain.model.ContentFormat;
import com.promptly.prompt.domain.model.Prompt;
import com.promptly.prompt.domain.model.PromptStatus;
import com.promptly.prompt.domain.model.PromptVersion;
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

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ClonePromptService}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ClonePromptService")
class ClonePromptServiceTest {

    @Mock private PromptPersistencePort persistencePort;
    @Mock private PromptHistoryPersistencePort historyRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    private ClonePromptService service;

    @BeforeEach
    void setUp() {
        service = new ClonePromptService(persistencePort, historyRepository, eventPublisher);
    }

    private Prompt sourcePrompt() {
        Prompt p = Prompt.builder()
                .name("Original Prompt")
                .description("Original description")
                .projectId("proj-1")
                .contentFormat(ContentFormat.TEXT)
                .tags(new HashSet<>(Set.of("tag1", "tag2")))
                .currentVersion(2)
                .status(PromptStatus.APPROVED)
                .build();
        p.setId("source-id");
        return p;
    }

    @Nested
    @DisplayName("success path")
    class SuccessTests {

        @BeforeEach
        void stub() {
            when(persistencePort.findById("source-id")).thenReturn(Mono.just(sourcePrompt()));
            when(historyRepository.findByPromptIdAndVersion("source-id", 2))
                    .thenReturn(Mono.just(PromptVersion.builder()
                            .versionNumber(2).content("v2 content").createdBy("alice").build()));
            when(persistencePort.save(any(Prompt.class)))
                    .thenAnswer(inv -> {
                        Prompt p = inv.getArgument(0);
                        p.setId("clone-id");
                        return Mono.just(p);
                    });
            when(historyRepository.save(anyString(), any(PromptVersion.class)))
                    .thenReturn(Mono.empty());
        }

        @Test
        @DisplayName("should clone with new name and DRAFT status")
        void shouldClone() {
            var cmd = new ClonePromptCommand("Cloned Prompt", null, null, "bob");

            StepVerifier.create(service.clonePrompt("source-id", cmd))
                    .assertNext(clone -> {
                        assertThat(clone.getName()).isEqualTo("Cloned Prompt");
                        assertThat(clone.getStatus()).isEqualTo(PromptStatus.DRAFT);
                        assertThat(clone.getCurrentVersion()).isEqualTo(1);
                        // Inherits project from source when not specified
                        assertThat(clone.getProjectId()).isEqualTo("proj-1");
                        // Contains latest content from source
                        assertThat(clone.getVersions().getFirst().getContent()).isEqualTo("v2 content");
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("should use custom project and description if provided")
        void shouldUseCustomProjectAndDesc() {
            var cmd = new ClonePromptCommand("Clone 2", "Custom desc", "proj-2", "bob");

            StepVerifier.create(service.clonePrompt("source-id", cmd))
                    .assertNext(clone -> {
                        assertThat(clone.getProjectId()).isEqualTo("proj-2");
                        assertThat(clone.getDescription()).isEqualTo("Custom desc");
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("should publish PromptCreated event")
        void shouldPublishEvent() {
            service.clonePrompt("source-id",
                    new ClonePromptCommand("Clone", null, null, "bob")).block();

            ArgumentCaptor<PromptCreated> captor = ArgumentCaptor.forClass(PromptCreated.class);
            verify(eventPublisher).publishEvent(captor.capture());
            assertThat(captor.getValue().promptId()).isEqualTo("clone-id");
        }
    }

    @Nested
    @DisplayName("failure path")
    class FailureTests {

        @Test
        @DisplayName("should throw ResourceNotFoundException when source doesn't exist")
        void shouldThrowNotFound() {
            when(persistencePort.findById("missing")).thenReturn(Mono.empty());

            StepVerifier.create(service.clonePrompt("missing",
                            new ClonePromptCommand("Clone", null, null, "bob")))
                    .expectError(ResourceNotFoundException.class)
                    .verify();

            verify(persistencePort, never()).save(any());
        }
    }
}
