package com.spectrayan.promptly.llmconfig.application.port.in;

import com.spectrayan.promptly.llmconfig.domain.model.LlmConfig;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

/**
 * Inbound port for managing LLM configurations (CRUD).
 * Handles per-project, per-feature overrides with encrypted API key storage.
 */
public interface ManageLlmConfigUseCase {

    /**
     * Returns all LLM configs for a project (all features).
     */
    Flux<LlmConfig> getConfigsByProject(String projectId);

    /**
     * Saves or updates an LLM config for a specific project and feature.
     * If an API key is provided in plaintext, it will be encrypted before storage (SaaS mode only).
     */
    Mono<LlmConfig> saveConfig(SaveConfigCommand command);

    /**
     * Resets a project's feature config back to platform defaults.
     */
    Mono<Void> resetConfig(String projectId, String feature);

    /**
     * Returns which fields are locked by environment variables for a given feature.
     */
    Set<String> getLockedFields(String feature);

    /**
     * Command object for saving LLM configuration.
     */
    record SaveConfigCommand(
            String projectId,
            String feature,
            String provider,
            String model,
            Double temperature,
            Integer maxTokens,
            String baseUrl,
            String plaintextApiKey,
            String updatedBy
    ) {}
}
