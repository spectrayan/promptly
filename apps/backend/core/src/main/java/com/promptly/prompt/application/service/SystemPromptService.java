package com.promptly.prompt.application.service;

import com.promptly.shared.domain.event.PromptUpdated;
import com.promptly.prompt.application.port.out.PromptHistoryPersistencePort;
import com.promptly.prompt.application.port.out.PromptPersistencePort;
import com.promptly.prompt.domain.model.Prompt;
import com.promptly.shared.systemprompt.SystemPromptPort;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Resolves system prompts using the Prompt Registry ("dogfooding").
 * <p>
 * Resolution precedence:
 * <ol>
 *   <li>Cached value (if available)</li>
 *   <li>Admin override: prompt named {@code <feature>-system-prompt}
 *       in the {@code __system__} project</li>
 *   <li>Classpath default: {@code resources/prompts/<feature>-system-prompt.md}</li>
 * </ol>
 * <p>
 * Cache is invalidated automatically when a {@link PromptUpdated} event
 * is published for the {@code __system__} project.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemPromptService implements SystemPromptPort {

    private final PromptPersistencePort promptRepository;
    private final PromptHistoryPersistencePort historyRepository;

    /** Classpath defaults loaded at startup. */
    private final Map<String, String> defaults = new ConcurrentHashMap<>();

    /** Resolved prompt cache: feature → prompt text. */
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    @PostConstruct
    void loadDefaults() {
        loadDefault("scanner");
        loadDefault("improver");
        log.info("Loaded {} default system prompts from classpath", defaults.size());
    }

    // ─── Public API ──────────────────────────────────────────────

    @Override
    public String getSystemPrompt(String feature) {
        return cache.computeIfAbsent(feature, this::resolve);
    }

    @Override
    public String getDefaultPrompt(String feature) {
        return defaults.getOrDefault(feature, "");
    }

    @Override
    public void invalidateCache(String feature) {
        log.debug("Invalidating system prompt cache for feature: {}", feature);
        cache.remove(feature);
    }

    // ─── Event-driven cache invalidation ─────────────────────────

    /**
     * Listens for prompt updates in the {@code __system__} project.
     * When a system prompt is modified by an admin, the cached value
     * is evicted so the next call to {@link #getSystemPrompt} fetches
     * the latest version.
     */
    @EventListener
    void onPromptUpdated(PromptUpdated event) {
        if (SYSTEM_PROJECT_ID.equals(event.projectId())) {
            log.info("System prompt updated (promptId={}), clearing cache", event.promptId());
            cache.clear();
        }
    }

    // ─── Resolution ──────────────────────────────────────────────

    /**
     * Resolves the system prompt for a feature.
     * Checks the {@code __system__} project in the Prompt Registry first,
     * then falls back to the classpath default.
     */
    private String resolve(String feature) {
        String promptName = feature + "-system-prompt";

        try {
            Prompt systemPrompt = promptRepository.findByProjectId(SYSTEM_PROJECT_ID)
                    .filter(p -> promptName.equals(p.getName()))
                    .next()
                    .block();

            if (systemPrompt != null && systemPrompt.getCurrentVersion() > 0) {
                String content = historyRepository
                        .findByPromptIdAndVersion(systemPrompt.getId(), systemPrompt.getCurrentVersion())
                        .map(v -> v.getContent())
                        .block();
                if (content != null && !content.isBlank()) {
                    log.info("Resolved system prompt for '{}' from __system__ project", feature);
                    return content;
                }
            }
        } catch (Exception e) {
            log.warn("Failed to resolve system prompt for '{}' from registry, using default: {}",
                    feature, e.getMessage());
        }

        log.debug("Using classpath default system prompt for '{}'", feature);
        return defaults.getOrDefault(feature, "");
    }

    // ─── Classpath loader ────────────────────────────────────────

    private void loadDefault(String feature) {
        String resourcePath = "prompts/" + feature + "-system-prompt.md";
        try {
            var resource = new ClassPathResource(resourcePath);
            if (resource.exists()) {
                String content = resource.getContentAsString(StandardCharsets.UTF_8);
                defaults.put(feature, content);
                log.debug("Loaded default system prompt: {}", resourcePath);
            } else {
                log.warn("Default system prompt resource not found: {}", resourcePath);
            }
        } catch (IOException e) {
            log.error("Failed to load default system prompt from {}: {}", resourcePath, e.getMessage());
        }
    }
}
