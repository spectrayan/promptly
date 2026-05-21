package com.spectrayan.promptly.prompt.application.service;

import com.spectrayan.promptly.prompt.application.port.out.PromptPersistencePort;
import com.spectrayan.promptly.prompt.domain.model.ContentFormat;
import com.spectrayan.promptly.prompt.domain.model.Prompt;
import com.spectrayan.promptly.prompt.domain.model.PromptStatus;
import com.spectrayan.promptly.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link GetPromptService}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GetPromptService")
class GetPromptServiceTest {

    @Mock private PromptPersistencePort persistencePort;

    private GetPromptService service;

    @BeforeEach
    void setUp() {
        service = new GetPromptService(persistencePort);
    }

    private Prompt samplePrompt(String id) {
        Prompt p = Prompt.builder()
                .name("prompt-" + id)
                .projectId("proj-1")
                .contentFormat(ContentFormat.TEXT)
                .status(PromptStatus.DRAFT)
                .currentVersion(0)
                .versions(new ArrayList<>())
                .build();
        p.setId(id);
        return p;
    }

    // ═══════════════════════════════════════════════════════════════
    // getPromptById
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("getPromptById")
    class GetByIdTests {

        @Test
        @DisplayName("should return prompt when found")
        void shouldReturnPrompt() {
            Prompt p = samplePrompt("p-1");
            when(persistencePort.findById("p-1")).thenReturn(Mono.just(p));

            StepVerifier.create(service.getPromptById("p-1"))
                    .assertNext(result -> {
                        assertThat(result.getId()).isEqualTo("p-1");
                        assertThat(result.getName()).isEqualTo("prompt-p-1");
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when not found")
        void shouldThrowNotFound() {
            when(persistencePort.findById("missing")).thenReturn(Mono.empty());

            StepVerifier.create(service.getPromptById("missing"))
                    .expectError(ResourceNotFoundException.class)
                    .verify();
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // getAllPrompts
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("getAllPrompts")
    class GetAllTests {

        @Test
        @DisplayName("should return all prompts")
        void shouldReturnAll() {
            when(persistencePort.findAll())
                    .thenReturn(Flux.just(samplePrompt("p-1"), samplePrompt("p-2")));

            StepVerifier.create(service.getAllPrompts())
                    .expectNextCount(2)
                    .verifyComplete();
        }

        @Test
        @DisplayName("should return paginated prompts")
        void shouldReturnPaginated() {
            Pageable pageable = PageRequest.of(0, 10);
            when(persistencePort.findAll(pageable))
                    .thenReturn(Flux.just(samplePrompt("p-1")));

            StepVerifier.create(service.getAllPrompts(pageable))
                    .expectNextCount(1)
                    .verifyComplete();

            verify(persistencePort).findAll(pageable);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // getPromptsByProjectId
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("getPromptsByProjectId")
    class GetByProjectTests {

        @Test
        @DisplayName("should return prompts filtered by project")
        void shouldFilterByProject() {
            when(persistencePort.findByProjectId("proj-1"))
                    .thenReturn(Flux.just(samplePrompt("p-1")));

            StepVerifier.create(service.getPromptsByProjectId("proj-1"))
                    .assertNext(p -> assertThat(p.getProjectId()).isEqualTo("proj-1"))
                    .verifyComplete();
        }

        @Test
        @DisplayName("should return paginated prompts filtered by project")
        void shouldFilterByProjectPaginated() {
            Pageable pageable = PageRequest.of(1, 5);
            when(persistencePort.findByProjectId("proj-1", pageable))
                    .thenReturn(Flux.just(samplePrompt("p-1")));

            StepVerifier.create(service.getPromptsByProjectId("proj-1", pageable))
                    .expectNextCount(1)
                    .verifyComplete();

            verify(persistencePort).findByProjectId("proj-1", pageable);
        }

        @Test
        @DisplayName("should return empty for unknown project")
        void shouldReturnEmptyForUnknown() {
            when(persistencePort.findByProjectId("nope"))
                    .thenReturn(Flux.empty());

            StepVerifier.create(service.getPromptsByProjectId("nope"))
                    .verifyComplete();
        }
    }
}
