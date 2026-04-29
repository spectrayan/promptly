package com.promptly.shared.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Root configuration properties for the Promptly platform.
 * <p>
 * Binds the entire {@code promptly.*} YAML namespace into a single, hierarchical,
 * type-safe bean.  All modules inject {@code PromptlyProperties} (or a nested child)
 * instead of using {@code @Value} annotations.
 *
 * <pre>
 * promptly:
 *   auth:
 *     provider: local
 *     jwt:
 *       secret: ...
 *   cors:
 *     allowed-origins: http://localhost:4200
 *   deployment:
 *     mode: self-hosted
 *   llm:
 *     provider: gemini
 *     model: gemini-2.5-flash
 *   rate-limit:
 *     enabled: false
 * </pre>
 */
@Data
@ConfigurationProperties(prefix = "promptly")
public class PromptlyProperties {

    /** Authentication & JWT configuration. */
    private Auth auth = new Auth();

    /** CORS configuration. */
    private Cors cors = new Cors();

    /** Deployment mode (SaaS vs self-hosted). */
    private Deployment deployment = new Deployment();

    /** LLM provider, model, and per-feature overrides. */
    private Llm llm = new Llm();

    /** Application-level rate limiting. */
    private RateLimit rateLimit = new RateLimit();

    // ═══════════════════════════════════════════════════════════════
    //  Auth
    // ═══════════════════════════════════════════════════════════════

    @Data
    public static class Auth {
        /** Auth provider: {@code local} (self-managed JWT) or {@code oidc} (external IdP). */
        private String provider = "local";

        /** JWT token configuration. */
        private Jwt jwt = new Jwt();

        @Data
        public static class Jwt {
            /** HMAC secret for signing JWTs (min 32 chars in production). */
            private String secret = "promptly-dev-secret-key-DO-NOT-USE-IN-PRODUCTION";

            /** Access token lifetime in milliseconds (default: 1 hour). */
            private long expirationMs = 3_600_000;

            /** Refresh token lifetime in milliseconds (default: 7 days). */
            private long refreshExpirationMs = 604_800_000;
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  CORS
    // ═══════════════════════════════════════════════════════════════

    @Data
    public static class Cors {
        /** Comma-separated allowed origins (mapped from YAML list or CSV string). */
        private String allowedOrigins = "http://localhost:4200,http://localhost:8080";

        /** Whether to send Access-Control-Allow-Credentials. */
        private boolean allowCredentials = true;
    }

    // ═══════════════════════════════════════════════════════════════
    //  Deployment
    // ═══════════════════════════════════════════════════════════════

    @Data
    public static class Deployment {
        /**
         * Deployment mode: {@code SAAS} or {@code SELF_HOSTED}.
         * <p>
         * In SaaS mode, tenants can override LLM config (including API keys
         * stored encrypted in DB).  In self-hosted mode, env vars are hard
         * locks and API keys are env-only.
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

    // ═══════════════════════════════════════════════════════════════
    //  LLM
    // ═══════════════════════════════════════════════════════════════

    @Data
    public static class Llm {
        /** Default LLM provider (e.g., openai, anthropic, gemini, ollama). */
        private String provider = "gemini";

        /** Default model name. */
        private String model = "gemini-2.5-flash";

        /** API key — should come from env var, never checked into YAML. */
        private String apiKey;

        /** Custom base URL (e.g., for Azure OpenAI or self-hosted Ollama). */
        private String baseUrl;

        /** Default temperature for LLM calls. */
        private double temperature = 0.3;

        /** Default max tokens for LLM responses. */
        private int maxTokens = 4096;

        /** AES encryption key for encrypting tenant API keys in MongoDB (SaaS mode). */
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

    // ═══════════════════════════════════════════════════════════════
    //  Rate Limiting
    // ═══════════════════════════════════════════════════════════════

    @Data
    public static class RateLimit {
        /** Whether application-level rate limiting is enabled. */
        private boolean enabled = false;

        /** Global per-IP requests per second. */
        private int requestsPerSecond = 100;

        /** Stricter per-IP limit for login/register endpoints (per minute). */
        private int loginRequestsPerMinute = 5;

        /** Wait time in ms for a rate-limit permit (0 = fail immediately). */
        private int timeout = 0;
    }
}
