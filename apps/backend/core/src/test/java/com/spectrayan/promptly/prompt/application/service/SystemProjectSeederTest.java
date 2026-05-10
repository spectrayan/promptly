package com.spectrayan.promptly.prompt.application.service;

import com.spectrayan.promptly.project.application.port.out.ProjectPersistencePort;
import com.spectrayan.promptly.project.domain.model.Project;
import com.spectrayan.promptly.prompt.application.port.out.PromptHistoryPersistencePort;
import com.spectrayan.promptly.prompt.application.port.out.PromptPersistencePort;
import com.spectrayan.promptly.prompt.domain.model.Prompt;
import com.spectrayan.promptly.shared.systemprompt.SystemPromptPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SystemProjectSeeder")
class SystemProjectSeederTest {

    @Mock ProjectPersistencePort projectRepository;
    @Mock PromptPersistencePort promptRepository;
    @Mock PromptHistoryPersistencePort historyRepository;
    @Mock SystemPromptPort systemPromptPort;

    SystemProjectSeeder seeder;

    @BeforeEach
    void setUp() {
        seeder = new SystemProjectSeeder(projectRepository, promptRepository, historyRepository, systemPromptPort);
    }

    @Nested
    @DisplayName("when __system__ project already exists")
    class WhenExists {

        @Test
        @DisplayName("should skip seeding entirely")
        void skipSeed() {
            when(projectRepository.existsByName("__system__")).thenReturn(Mono.just(true));

            seeder.doSeed().block();

            verify(projectRepository, never()).save(any());
            verify(promptRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("when __system__ project does not exist")
    class WhenDoesNotExist {

        @BeforeEach
        void setUpMocks() {
            when(projectRepository.existsByName("__system__")).thenReturn(Mono.just(false));

            var savedProject = Project.builder().id("sys-id-123").name("__system__").build();
            when(projectRepository.save(any(Project.class))).thenReturn(Mono.just(savedProject));

            lenient().when(systemPromptPort.getDefaultPrompt("scanner")).thenReturn("Scanner default content");
            lenient().when(systemPromptPort.getDefaultPrompt("improver")).thenReturn("Improver default content");

            when(promptRepository.save(any(Prompt.class))).thenAnswer(inv -> {
                Prompt p = inv.getArgument(0);
                p.setId("prompt-" + p.getName());
                return Mono.just(p);
            });

            lenient().when(historyRepository.save(anyString(), any())).thenReturn(Mono.empty());
        }

        @Test
        @DisplayName("should create the __system__ project")
        void createsProject() {
            seeder.doSeed().block();

            ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
            verify(projectRepository).save(captor.capture());

            Project created = captor.getValue();
            assertThat(created.getName()).isEqualTo("__system__");
            assertThat(created.getCreatedBy()).isEqualTo("system");
            assertThat(created.getTags()).contains("system", "internal");
        }

        @Test
        @DisplayName("should seed scanner and improver prompts")
        void seedsPrompts() {
            seeder.doSeed().block();

            ArgumentCaptor<Prompt> captor = ArgumentCaptor.forClass(Prompt.class);
            verify(promptRepository, times(2)).save(captor.capture());

            var names = captor.getAllValues().stream().map(Prompt::getName).toList();
            assertThat(names).containsExactlyInAnyOrder(
                    "scanner-system-prompt",
                    "improver-system-prompt"
            );
        }

        @Test
        @DisplayName("should set project ID on seeded prompts")
        void setsProjectId() {
            seeder.doSeed().block();

            ArgumentCaptor<Prompt> captor = ArgumentCaptor.forClass(Prompt.class);
            verify(promptRepository, times(2)).save(captor.capture());

            captor.getAllValues().forEach(p ->
                    assertThat(p.getProjectId()).isEqualTo("sys-id-123")
            );
        }

        @Test
        @DisplayName("should create version 1 with default content")
        void createsInitialVersion() {
            seeder.doSeed().block();

            ArgumentCaptor<Prompt> captor = ArgumentCaptor.forClass(Prompt.class);
            verify(promptRepository, times(2)).save(captor.capture());

            captor.getAllValues().forEach(p -> {
                assertThat(p.getVersions()).hasSize(1);
                assertThat(p.getCurrentVersion()).isEqualTo(1);
            });
        }
    }

    @Nested
    @DisplayName("edge cases")
    class EdgeCases {

        @Test
        @DisplayName("should skip prompt when default is blank")
        void skipBlankDefault() {
            when(projectRepository.existsByName("__system__")).thenReturn(Mono.just(false));

            var savedProject = Project.builder().id("sys-id").name("__system__").build();
            when(projectRepository.save(any(Project.class))).thenReturn(Mono.just(savedProject));

            when(systemPromptPort.getDefaultPrompt("scanner")).thenReturn("");
            when(systemPromptPort.getDefaultPrompt("improver")).thenReturn("Valid content");

            when(promptRepository.save(any(Prompt.class))).thenAnswer(inv -> {
                Prompt p = inv.getArgument(0);
                p.setId("prompt-" + p.getName());
                return Mono.just(p);
            });

            lenient().when(historyRepository.save(anyString(), any())).thenReturn(Mono.empty());

            seeder.doSeed().block();

            // Only the improver prompt should be saved (scanner had blank default)
            verify(promptRepository, times(1)).save(any(Prompt.class));
        }
    }
}
