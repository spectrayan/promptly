package com.spectrayan.promptly.prompt.application.service;

import com.spectrayan.promptly.shared.domain.event.PromptUpdated;
import com.spectrayan.promptly.prompt.application.port.in.UpdatePromptUseCase.UpdatePromptCommand;
import com.spectrayan.promptly.prompt.application.port.out.PromptHistoryPersistencePort;
import com.spectrayan.promptly.prompt.application.port.out.PromptPersistencePort;
import com.spectrayan.promptly.prompt.domain.model.ContentFormat;
import com.spectrayan.promptly.prompt.domain.model.Prompt;
import com.spectrayan.promptly.prompt.domain.model.PromptStatus;
import com.spectrayan.promptly.prompt.domain.model.PromptVersion;
import com.spectrayan.promptly.shared.exception.ResourceNotFoundException;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link UpdatePromptService}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UpdatePromptService")
class UpdatePromptServiceTest {

    @Mock private PromptPersistencePort persistencePort;
    @Mock private PromptHistoryPersistencePort historyRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    private UpdatePromptService service;

    @BeforeEach
    void setUp() {
        service = new UpdatePromptService(persistencePort, historyRepository, eventPublisher);
    }

    private Prompt existingPrompt() {
        Prompt p = Prompt.builder()
                .name("Test Prompt")
                .projectId("proj-1")
                .contentFormat(ContentFormat.TEXT)
                .status(PromptStatus.DRAFT)
                .currentVersion(1)
                .versions(new ArrayList<>(List.of(
                        PromptVersion.builder()
                                .versionNumber(1)
                                .content("Original content")
                                .createdBy("alice")
                                .build()
                )))
                .build();
        p.setId("p-1");
        return p;
    }

    @Nested
    @DisplayName("success path")
    class SuccessTests {

        @Test
        @DisplayName("should update draft in place and publish event")
        void shouldCreateNewVersion() {
            Prompt existing = existingPrompt();
            when(persistencePort.findById("p-1")).thenReturn(Mono.just(existing));
            when(persistencePort.save(any(Prompt.class)))
                    .thenAnswer(inv -> Mono.just(inv.getArgument(0)));
            when(historyRepository.save(anyString(), any(PromptVersion.class)))
                    .thenReturn(Mono.empty());

            var command = new UpdatePromptCommand("Updated content", "Fixed typo", "bob");

            StepVerifier.create(service.updatePrompt("p-1", command))
                    .assertNext(updated -> {
                        // DRAFT prompts update the latest version in place (no version bump)
                        assertThat(updated.getCurrentVersion()).isEqualTo(1);
                        assertThat(updated.getVersions()).hasSize(1);
                        assertThat(updated.getVersions().get(0).getContent()).isEqualTo("Updated content");
                        assertThat(updated.getVersions().get(0).getCreatedBy()).isEqualTo("bob");
                    })
                    .verifyComplete();

            ArgumentCaptor<PromptUpdated> captor = ArgumentCaptor.forClass(PromptUpdated.class);
            verify(eventPublisher).publishEvent(captor.capture());
            assertThat(captor.getValue().promptId()).isEqualTo("p-1");
            assertThat(captor.getValue().version()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("failure path")
    class FailureTests {

        @Test
        @DisplayName("should throw ResourceNotFoundException when prompt doesn't exist")
        void shouldThrowNotFound() {
            when(persistencePort.findById("missing")).thenReturn(Mono.empty());

            StepVerifier.create(service.updatePrompt("missing",
                            new UpdatePromptCommand("content", "msg", "alice")))
                    .expectError(ResourceNotFoundException.class)
                    .verify();

            verify(persistencePort, never()).save(any());
            verifyNoInteractions(eventPublisher);
        }
    }
}
