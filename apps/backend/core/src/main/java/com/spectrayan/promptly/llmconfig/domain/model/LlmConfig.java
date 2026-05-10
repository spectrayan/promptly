package com.spectrayan.promptly.llmconfig.domain.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * Domain model for a tenant's LLM configuration.
 * Stored per project + feature combination (e.g., project "proj-001" + feature "scanner").
 * <p>
 * This is a pure POJO — no framework annotations. MongoDB document mapping
 * lives in the infrastructure layer.
 */
@Data
@Builder
public class LlmConfig {

    private String id;

    /** The project this config belongs to. Null means platform-global default. */
    private String projectId;

    /**
     * Feature scope: "global", "scanner", "improver", "embedding".
     * Global applies unless a feature-specific override exists.
     */
    private String feature;

    /** LLM provider (e.g., openai, anthropic, gemini, ollama). */
    private String provider;

    /** Model name (e.g., gpt-4o, claude-sonnet-4, gemini-2.5-flash). */
    private String model;

    /** Generation temperature. */
    private Double temperature;

    /** Max tokens for LLM responses. */
    private Integer maxTokens;

    /** Custom base URL for the provider API. */
    private String baseUrl;

    /**
     * Encrypted API key (SaaS mode only).
     * Format: "enc:aes256:<base64-encoded-iv+ciphertext>"
     * In self-hosted mode, this field is always null — keys come from env vars.
     */
    private String encryptedApiKey;

    private Instant updatedAt;
    private String updatedBy;
}
