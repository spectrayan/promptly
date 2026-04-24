package com.promptly.llmconfig.domain.model;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

/**
 * The fully resolved LLM configuration for a specific feature invocation.
 * This is the output of the config resolver after merging YAML → DB → Env layers.
 */
@Data
@Builder
public class ResolvedLlmConfig {

    private String provider;
    private String model;
    private double temperature;
    private int maxTokens;
    private String baseUrl;

    /** The decrypted API key — never exposed via REST API. */
    private String apiKey;

    /** Which fields are locked by environment variables (UI renders these as read-only). */
    private Set<String> lockedFields;

    /** Source tracking: where each field value came from. */
    private ConfigSource providerSource;
    private ConfigSource modelSource;
    private ConfigSource temperatureSource;
    private ConfigSource apiKeySource;

    public enum ConfigSource {
        YAML_DEFAULT,
        DATABASE,
        ENVIRONMENT
    }

    /** Returns true if an API key is available (from any source). */
    public boolean hasApiKey() {
        return apiKey != null && !apiKey.isBlank();
    }
}
