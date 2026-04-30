package com.promptly.prompt.application.service;

import com.promptly.shared.domain.event.PromptUpdated;
import com.promptly.prompt.application.port.out.PromptPersistencePort;
import com.promptly.prompt.domain.model.*;
import com.promptly.shared.systemprompt.SystemPromptPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
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

    private SystemPromptService service;

    @BeforeEach
    void setUp() {
        service = new SystemPromptService(persistencePort);
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
                    .contains("prompt security analyzer");
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

        @BeforeEach
        void stub() {
            when(persistencePort.findByProjectId(SystemPromptPort.SYSTEM_PROJECT_ID))
                    .thenReturn(Flux.empty());
        }

        @Test
        @DisplayName("should fall back to classpath default for scanner")
        void shouldFallbackScanner() {
            String prompt = service.getSystemPrompt("scanner");
            assertThat(prompt).contains("prompt security analyzer");
        }

        @Test
        @DisplayName("should fall back to classpath default for improver")
        void shouldFallbackImprover() {
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
            Prompt systemPrompt = buildSystemPrompt("scanner-system-prompt", "Custom scanner instructions");
            when(persistencePort.findByProjectId(SystemPromptPort.SYSTEM_PROJECT_ID))
                    .thenReturn(Flux.just(systemPrompt));

            String prompt = service.getSystemPrompt("scanner");
            assertThat(prompt).isEqualTo("Custom scanner instructions");
        }

        @Test
        @DisplayName("should not pick up unrelated system prompts")
        void shouldFilterByName() {
            Prompt wrongPrompt = buildSystemPrompt("improver-system-prompt", "Improver content");
            when(persistencePort.findByProjectId(SystemPromptPort.SYSTEM_PROJECT_ID))
                    .thenReturn(Flux.just(wrongPrompt));

            String prompt = service.getSystemPrompt("scanner");
            // Should fall back to default since no scanner-system-prompt exists
            assertThat(prompt).contains("prompt security analyzer");
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // Caching
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("caching behavior")
    class CachingTests {

        @BeforeEach
        void stub() {
            when(persistencePort.findByProjectId(SystemPromptPort.SYSTEM_PROJECT_ID))
                    .thenReturn(Flux.empty());
        }

        @Test
        @DisplayName("should cache resolved prompt — only one DB call per feature")
        void shouldCacheResult() {
            service.getSystemPrompt("scanner");
            service.getSystemPrompt("scanner");
            service.getSystemPrompt("scanner");

            // Only called once — subsequent calls are served from cache
            verify(persistencePort, times(1)).findByProjectId(SystemPromptPort.SYSTEM_PROJECT_ID);
        }

        @Test
        @DisplayName("should re-resolve after cache invalidation")
        void shouldReResolveAfterInvalidation() {
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

        @BeforeEach
        void stub() {
            when(persistencePort.findByProjectId(SystemPromptPort.SYSTEM_PROJECT_ID))
                    .thenReturn(Flux.empty());
        }

        @Test
        @DisplayName("should clear cache on PromptUpdated from __system__ project")
        void shouldInvalidateOnSystemEvent() {
            service.getSystemPrompt("scanner");

            // Simulate event from __system__ project
            service.onPromptUpdated(new PromptUpdated("prompt-1", SystemPromptPort.SYSTEM_PROJECT_ID, 2));

            service.getSystemPrompt("scanner");

            // Should have been called twice: initial + after invalidation
            verify(persistencePort, times(2)).findByProjectId(SystemPromptPort.SYSTEM_PROJECT_ID);
        }

        @Test
        @DisplayName("should NOT clear cache on PromptUpdated from regular project")
        void shouldNotInvalidateOnRegularEvent() {
            service.getSystemPrompt("scanner");

            // Event from a regular project — should NOT invalidate cache
            service.onPromptUpdated(new PromptUpdated("prompt-1", "proj-regular", 2));

            service.getSystemPrompt("scanner");

            // Still only one call — cache was not cleared
            verify(persistencePort, times(1)).findByProjectId(SystemPromptPort.SYSTEM_PROJECT_ID);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // Helpers
    // ═══════════════════════════════════════════════════════════════

    private Prompt buildSystemPrompt(String name, String content) {
        var versions = new ArrayList<PromptVersion>();
        versions.add(PromptVersion.builder()
                .versionNumber(1)
                .content(content)
                .changeMessage("Initial system prompt")
                .createdBy("admin")
                .build());

        return Prompt.builder()
                .id("sys-" + name)
                .name(name)
                .description("System prompt")
                .projectId(SystemPromptPort.SYSTEM_PROJECT_ID)
                .contentFormat(ContentFormat.MARKDOWN)
                .tags(Set.of("system"))
                .status(PromptStatus.APPROVED)
                .currentVersion(1)
                .versions(versions)
                .build();
    }
}
