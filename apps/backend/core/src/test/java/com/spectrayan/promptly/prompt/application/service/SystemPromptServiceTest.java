package com.spectrayan.promptly.prompt.application.service;

import com.spectrayan.promptly.shared.domain.event.PromptUpdated;
import com.spectrayan.promptly.prompt.application.port.out.PromptHistoryPersistencePort;
import com.spectrayan.promptly.prompt.application.port.out.PromptPersistencePort;
import com.spectrayan.promptly.prompt.domain.model.*;
import com.spectrayan.promptly.shared.systemprompt.SystemPromptPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link SystemPromptService}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SystemPromptService")
class SystemPromptServiceTest {

    @Mock private PromptPersistencePort persistencePort;
    @Mock private PromptHistoryPersistencePort historyRepository;

    private SystemPromptService service;

    @BeforeEach
    void setUp() {
        service = new SystemPromptService(persistencePort, historyRepository);
        // Simulate @PostConstruct — loads classpath defaults
        service.loadDefaults();
    }

    // ═══════════════════════════════════════════════════════════════
    // Classpath defaults
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("classpath defaults")
    class ClasspathDefaultTests {

        @Test
        @DisplayName("should load scanner default prompt from classpath")
        void shouldLoadScannerDefault() {
            String defaultPrompt = service.getDefaultPrompt("scanner");
            assertThat(defaultPrompt)
                    .isNotBlank()
                    .contains("expert AI security analyst");
        }

        @Test
        @DisplayName("should load improver default prompt from classpath")
        void shouldLoadImproverDefault() {
            String defaultPrompt = service.getDefaultPrompt("improver");
            assertThat(defaultPrompt)
                    .isNotBlank()
                    .contains("expert prompt engineer");
        }

        @Test
        @DisplayName("should return empty string for unknown feature")
        void shouldReturnEmptyForUnknown() {
            assertThat(service.getDefaultPrompt("nonexistent")).isEmpty();
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // Resolution: fallback to classpath when no registry override
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("resolution with no registry override")
    class FallbackTests {

        @Test
        @DisplayName("should fall back to classpath default for scanner")
        void shouldFallbackScanner() {
            service.invalidateCache("scanner");
            when(persistencePort.findByProjectId(SystemPromptPort.SYSTEM_PROJECT_ID))
                    .thenReturn(Flux.empty());

            String prompt = service.getSystemPrompt("scanner");
            assertThat(prompt).contains("expert AI security analyst");
        }

        @Test
        @DisplayName("should fall back to classpath default for improver")
        void shouldFallbackImprover() {
            service.invalidateCache("improver");
            when(persistencePort.findByProjectId(SystemPromptPort.SYSTEM_PROJECT_ID))
                    .thenReturn(Flux.empty());

            String prompt = service.getSystemPrompt("improver");
            assertThat(prompt).contains("expert prompt engineer");
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // Resolution: registry override takes precedence
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("resolution with registry override")
    class RegistryOverrideTests {

        @Test
        @DisplayName("should use registry prompt when available")
        void shouldUseRegistryPrompt() {
            // Invalidate cache so getSystemPrompt calls resolve()
            service.invalidateCache("scanner");
            Prompt systemPrompt = buildSystemPrompt("scanner-system-prompt", 1);
            when(persistencePort.findByProjectId(SystemPromptPort.SYSTEM_PROJECT_ID))
                    .thenReturn(Flux.just(systemPrompt));
            when(historyRepository.findByPromptIdAndVersion("sys-scanner-system-prompt", 1))
                    .thenReturn(Mono.just(PromptVersion.builder()
                            .versionNumber(1)
                            .content("Custom scanner instructions")
                            .createdBy("admin")
                            .build()));

            String prompt = service.getSystemPrompt("scanner");
            assertThat(prompt).isEqualTo("Custom scanner instructions");
        }

        @Test
        @DisplayName("should not pick up unrelated system prompts")
        void shouldFilterByName() {
            service.invalidateCache("scanner");
            Prompt wrongPrompt = buildSystemPrompt("improver-system-prompt", 1);
            when(persistencePort.findByProjectId(SystemPromptPort.SYSTEM_PROJECT_ID))
                    .thenReturn(Flux.just(wrongPrompt));

            String prompt = service.getSystemPrompt("scanner");
            // Should fall back to default since no scanner-system-prompt exists
            assertThat(prompt).contains("expert AI security analyst");
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // Caching
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("caching behavior")
    class CachingTests {

        @Test
        @DisplayName("should cache resolved prompt -- only one DB call per feature")
        void shouldCacheResult() {
            // Invalidate to force resolve() path
            service.invalidateCache("scanner");
            when(persistencePort.findByProjectId(SystemPromptPort.SYSTEM_PROJECT_ID))
                    .thenReturn(Flux.empty());

            service.getSystemPrompt("scanner");
            service.getSystemPrompt("scanner");
            service.getSystemPrompt("scanner");

            // Only called once -- subsequent calls are served from cache
            verify(persistencePort, times(1)).findByProjectId(SystemPromptPort.SYSTEM_PROJECT_ID);
        }

        @Test
        @DisplayName("should re-resolve after cache invalidation")
        void shouldReResolveAfterInvalidation() {
            service.invalidateCache("scanner");
            when(persistencePort.findByProjectId(SystemPromptPort.SYSTEM_PROJECT_ID))
                    .thenReturn(Flux.empty());

            service.getSystemPrompt("scanner");
            service.invalidateCache("scanner");
            service.getSystemPrompt("scanner");

            verify(persistencePort, times(2)).findByProjectId(SystemPromptPort.SYSTEM_PROJECT_ID);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // Event-driven invalidation
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("event-driven cache invalidation")
    class EventTests {

        @Test
        @DisplayName("should clear cache on PromptUpdated from __system__ project")
        void shouldInvalidateOnSystemEvent() {
            // Invalidate and stub so we can track DB calls
            service.invalidateCache("scanner");
            when(persistencePort.findByProjectId(SystemPromptPort.SYSTEM_PROJECT_ID))
                    .thenReturn(Flux.empty());

            service.getSystemPrompt("scanner");

            // Simulate event from __system__ project
            service.onPromptUpdated(new PromptUpdated("prompt-1", "Scanner Prompt", SystemPromptPort.SYSTEM_PROJECT_ID, 2));

            service.getSystemPrompt("scanner");

            // Should have been called twice: initial + after invalidation
            verify(persistencePort, times(2)).findByProjectId(SystemPromptPort.SYSTEM_PROJECT_ID);
        }

        @Test
        @DisplayName("should NOT clear cache on PromptUpdated from regular project")
        void shouldNotInvalidateOnRegularEvent() {
            service.invalidateCache("scanner");
            when(persistencePort.findByProjectId(SystemPromptPort.SYSTEM_PROJECT_ID))
                    .thenReturn(Flux.empty());

            service.getSystemPrompt("scanner");

            // Event from a regular project -- should NOT invalidate cache
            service.onPromptUpdated(new PromptUpdated("prompt-1", "Regular Prompt", "proj-regular", 2));

            service.getSystemPrompt("scanner");

            // Still only one call -- cache was not cleared
            verify(persistencePort, times(1)).findByProjectId(SystemPromptPort.SYSTEM_PROJECT_ID);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // Helpers
    // ═══════════════════════════════════════════════════════════════

    private Prompt buildSystemPrompt(String name, int currentVersion) {
        return Prompt.builder()
                .id("sys-" + name)
                .name(name)
                .description("System prompt")
                .projectId(SystemPromptPort.SYSTEM_PROJECT_ID)
                .contentFormat(ContentFormat.MARKDOWN)
                .tags(Set.of("system"))
                .status(PromptStatus.APPROVED)
                .currentVersion(currentVersion)
                .build();
    }
}
