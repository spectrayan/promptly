package com.promptly.llmconfig.infrastructure.web;

import com.promptly.llmconfig.application.port.in.ManageLlmConfigUseCase;
import com.promptly.llmconfig.application.port.in.ResolveLlmConfigUseCase;
import com.promptly.llmconfig.domain.model.ResolvedLlmConfig;
import com.promptly.shared.config.CredentialEncryptionService;
import com.promptly.shared.config.DeploymentProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for LLM configuration management.
 * Allows admins to view, update, and reset LLM provider settings per project/feature.
 */
@RestController
@RequestMapping("/api/v1/settings/llm-configs")
@RequiredArgsConstructor
public class LlmConfigController {

    private final ResolveLlmConfigUseCase resolveConfigUseCase;
    private final ManageLlmConfigUseCase manageConfigUseCase;
    private final CredentialEncryptionService encryptionService;
    private final DeploymentProperties deploymentProps;

    /**
     * Get the resolved LLM configuration for a project + feature.
     * Returns the merged result with source tracking and lock indicators.
     */
    @GetMapping("/resolved")
    public Mono<ResponseEntity<Map<String, Object>>> getResolvedConfig(
            @RequestParam String projectId,
            @RequestParam(defaultValue = "global") String feature) {

        return resolveConfigUseCase.resolve(projectId, feature)
                .map(resolved -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("config", buildConfigResponse(resolved));
                    response.put("lockedFields", resolved.getLockedFields());
                    response.put("source", buildSourceMap(resolved));
                    response.put("deploymentMode", deploymentProps.getMode().name().toLowerCase());
                    response.put("encryptionAvailable", encryptionService.isConfigured());
                    return ResponseEntity.ok(response);
                });
    }

    /**
     * Get all LLM configs for a project (raw DB values, not resolved).
     */
    @GetMapping
    public Mono<ResponseEntity<List<Map<String, Object>>>> getProjectConfigs(
            @RequestParam String projectId) {

        return manageConfigUseCase.getConfigsByProject(projectId)
                .map(config -> {
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("feature", config.getFeature());
                    entry.put("provider", config.getProvider());
                    entry.put("model", config.getModel());
                    entry.put("temperature", config.getTemperature());
                    entry.put("maxTokens", config.getMaxTokens());
                    entry.put("baseUrl", config.getBaseUrl());
                    entry.put("apiKeyConfigured", config.getEncryptedApiKey() != null);
                    entry.put("updatedAt", config.getUpdatedAt());
                    entry.put("updatedBy", config.getUpdatedBy());
                    return entry;
                })
                .collectList()
                .map(ResponseEntity::ok);
    }

    /**
     * Save or update LLM config for a project + feature.
     */
    @PutMapping
    public Mono<ResponseEntity<Map<String, Object>>> saveConfig(@RequestBody SaveLlmConfigRequest request) {
        var command = new ManageLlmConfigUseCase.SaveConfigCommand(
                request.projectId(),
                request.feature(),
                request.provider(),
                request.model(),
                request.temperature(),
                request.maxTokens(),
                request.baseUrl(),
                request.apiKey(),
                "admin" // TODO: extract from auth context
        );
        return manageConfigUseCase.saveConfig(command)
                .map(saved -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "saved");
                    response.put("feature", saved.getFeature());
                    response.put("provider", saved.getProvider());
                    response.put("model", saved.getModel());
                    response.put("apiKeyConfigured", saved.getEncryptedApiKey() != null);
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(IllegalArgumentException.class,
                        e -> Mono.just(ResponseEntity.badRequest().body(Map.of("error", e.getMessage()))));
    }

    /**
     * Reset a project's feature config back to platform defaults.
     */
    @DeleteMapping
    public Mono<ResponseEntity<Map<String, String>>> resetConfig(
            @RequestParam String projectId,
            @RequestParam(defaultValue = "global") String feature) {

        return manageConfigUseCase.resetConfig(projectId, feature)
                .then(Mono.just(ResponseEntity.ok(
                        Map.of("status", "reset", "message",
                               "Config for " + feature + " reset to platform defaults"))));
    }

    // ─── Request / Response DTOs ──────────────────────────────────

    public record SaveLlmConfigRequest(
            String projectId,
            String feature,
            String provider,
            String model,
            Double temperature,
            Integer maxTokens,
            String baseUrl,
            String apiKey   // plaintext — will be encrypted before storage
    ) {}

    private Map<String, Object> buildConfigResponse(ResolvedLlmConfig resolved) {
        Map<String, Object> config = new HashMap<>();
        config.put("provider", resolved.getProvider());
        config.put("model", resolved.getModel());
        config.put("temperature", resolved.getTemperature());
        config.put("maxTokens", resolved.getMaxTokens());
        config.put("baseUrl", resolved.getBaseUrl());
        config.put("apiKeyConfigured", resolved.hasApiKey());
        if (resolved.hasApiKey()) {
            config.put("apiKeyHint", encryptionService.mask(resolved.getApiKey()));
        }
        return config;
    }

    private Map<String, String> buildSourceMap(ResolvedLlmConfig resolved) {
        Map<String, String> source = new HashMap<>();
        if (resolved.getProviderSource() != null) source.put("provider", resolved.getProviderSource().name());
        if (resolved.getModelSource() != null) source.put("model", resolved.getModelSource().name());
        if (resolved.getTemperatureSource() != null) source.put("temperature", resolved.getTemperatureSource().name());
        if (resolved.getApiKeySource() != null) source.put("apiKey", resolved.getApiKeySource().name());
        return source;
    }
}
