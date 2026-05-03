package com.promptly.prompt.application.service;

import com.promptly.prompt.application.port.out.PromptHistoryPersistencePort;
import com.promptly.prompt.application.port.out.PromptPersistencePort;
import com.promptly.prompt.domain.model.ContentFormat;
import com.promptly.prompt.domain.model.Prompt;
import com.promptly.prompt.domain.model.PromptStatus;
import com.promptly.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link DeletePromptService}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DeletePromptService")
class DeletePromptServiceTest {

    @Mock private PromptPersistencePort persistencePort;
    @Mock private PromptHistoryPersistencePort historyRepository;

    private DeletePromptService service;

    @BeforeEach
    void setUp() {
        service = new DeletePromptService(persistencePort, historyRepository);
    }

    private Prompt deletablePrompt() {
        Prompt p = Prompt.builder()
                .name("Deletable Prompt")
                .projectId("proj-1")
                .contentFormat(ContentFormat.TEXT)
                .status(PromptStatus.DRAFT)
                .currentVersion(0)
                .versions(new ArrayList<>())
                .build();
        p.setId("p-1");
        return p;
    }

    private Prompt nonDeletablePrompt() {
        Prompt p = Prompt.builder()
                .name("Approved Prompt")
                .projectId("proj-1")
                .contentFormat(ContentFormat.TEXT)
                .status(PromptStatus.APPROVED)
                .currentVersion(1)
                .versions(new ArrayList<>())
                .build();
        p.setId("p-2");
        return p;
    }

    @Nested
    @DisplayName("success path")
    class SuccessTests {

        @Test
        @DisplayName("should delete a DRAFT prompt")
        void shouldDeleteDraft() {
            when(persistencePort.findById("p-1")).thenReturn(Mono.just(deletablePrompt()));
            when(historyRepository.deleteByPromptId("p-1")).thenReturn(Mono.empty());
            when(persistencePort.deleteById("p-1")).thenReturn(Mono.empty());

            StepVerifier.create(service.deletePrompt("p-1"))
                    .verifyComplete();

            verify(historyRepository).deleteByPromptId("p-1");
            verify(persistencePort).deleteById("p-1");
        }
    }

    @Nested
    @DisplayName("failure path")
    class FailureTests {

        @Test
        @DisplayName("should throw ResourceNotFoundException when prompt doesn't exist")
        void shouldThrowNotFound() {
            when(persistencePort.findById("missing")).thenReturn(Mono.empty());

            StepVerifier.create(service.deletePrompt("missing"))
                    .expectError(ResourceNotFoundException.class)
                    .verify();

            verify(persistencePort, never()).deleteById(any());
        }

        @Test
        @DisplayName("should throw IllegalStateException for APPROVED prompt")
        void shouldRejectApprovedDelete() {
            when(persistencePort.findById("p-2")).thenReturn(Mono.just(nonDeletablePrompt()));

            StepVerifier.create(service.deletePrompt("p-2"))
                    .expectError(IllegalStateException.class)
                    .verify();

            verify(persistencePort, never()).deleteById(any());
        }
    }
}
