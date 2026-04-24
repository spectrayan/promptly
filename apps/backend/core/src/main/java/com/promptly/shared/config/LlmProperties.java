package com.promptly.shared.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Platform-level LLM configuration from application.yml / environment variables.
 * These serve as defaults that can be overridden by tenant-specific DB config (in SaaS mode)
 * or locked by env vars (in self-hosted mode).
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "promptly.llm")
public class LlmProperties {

    /** Default LLM provider (e.g., openai, anthropic, gemini, ollama). */
    private String provider = "gemini";

    /** Default model name. */
    private String model = "gemini-2.5-flash";

    /** API key — should come from env var PROMPTLY_LLM_API_KEY, never checked into YAML. */
    private String apiKey;

    /** Custom base URL for the provider API (e.g., for Azure OpenAI or self-hosted Ollama). */
    private String baseUrl;

    /** Default temperature for LLM calls. */
    private double temperature = 0.3;

    /** Default max tokens for LLM responses. */
    private int maxTokens = 4096;

    /** AES encryption key for encrypting tenant API keys stored in MongoDB (SaaS mode). */
    private String credentialEncryptionKey;

    /** Per-feature overrides — null fields mean "use global default". */
    private FeatureConfig scanner = new FeatureConfig();
    private FeatureConfig improver = new FeatureConfig();
    private FeatureConfig embedding = new FeatureConfig();

    @Data
    public static class FeatureConfig {
        private String provider;
        private String model;
        private Double temperature;
        private Integer maxTokens;
        private String apiKey;
    }
}
