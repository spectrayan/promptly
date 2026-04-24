package com.promptly.shared.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Deployment-mode configuration.
 * <p>
 * Controls whether the application runs in SaaS or self-hosted mode,
 * which affects LLM config precedence, credential storage strategy,
 * and tenant isolation behavior.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "promptly.deployment")
public class DeploymentProperties {

    /**
     * Deployment mode: {@code saas} or {@code self-hosted}.
     * <p>
     * In SaaS mode, tenants can override LLM config (including API keys stored encrypted in DB).
     * In self-hosted mode, env vars are hard locks and API keys are env-only.
     */
    private DeploymentMode mode = DeploymentMode.SELF_HOSTED;

    public enum DeploymentMode {
        SAAS,
        SELF_HOSTED
    }

    public boolean isSaas() {
        return mode == DeploymentMode.SAAS;
    }

    public boolean isSelfHosted() {
        return mode == DeploymentMode.SELF_HOSTED;
    }
}
