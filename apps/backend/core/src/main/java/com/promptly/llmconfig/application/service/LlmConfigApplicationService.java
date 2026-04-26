package com.promptly.llmconfig.application.service;

import com.promptly.llmconfig.application.port.in.ManageLlmConfigUseCase;
import com.promptly.llmconfig.application.port.in.ResolveLlmConfigUseCase;
import com.promptly.llmconfig.application.port.out.LlmConfigPersistencePort;
import com.promptly.llmconfig.domain.model.LlmConfig;
import com.promptly.llmconfig.domain.model.ResolvedLlmConfig;
import com.promptly.llmconfig.domain.model.ResolvedLlmConfig.ConfigSource;
import com.promptly.shared.config.CredentialEncryptionService;
import com.promptly.shared.config.DeploymentProperties;
import com.promptly.shared.config.LlmProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * LLM Config application service — implements resolution, CRUD, and encryption.
 * <p>
 * Resolution precedence depends on deployment mode:
 * <ul>
 *   <li><b>SaaS</b>: Tenant DB &gt; Platform Env &gt; YAML</li>
 *   <li><b>Self-hosted</b>: Env &gt; DB &gt; YAML</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LlmConfigApplicationService implements ResolveLlmConfigUseCase, ManageLlmConfigUseCase {

    private final LlmProperties yamlProps;
    private final DeploymentProperties deploymentProps;
    private final LlmConfigPersistencePort configRepo;
    private final CredentialEncryptionService encryptionService;
    private final Environment springEnv;

    // ─── Resolution ───────────────────────────────────────────────

    /**
     * Resolves the effective LLM configuration for a given project and feature.
     */
    @Override
    public Mono<ResolvedLlmConfig> resolve(String projectId, String feature) {
        return configRepo.findByProjectIdAndFeature(projectId, feature)
                .switchIfEmpty(configRepo.findByProjectIdAndFeature(projectId, "global"))
                .map(dbConfig -> mergeConfig(dbConfig, feature))
                .defaultIfEmpty(buildFromDefaults(feature));
    }

    private ResolvedLlmConfig mergeConfig(LlmConfig dbConfig, String feature) {
        var defaults = getFeatureDefaults(feature);

        if (deploymentProps.isSaas()) {
            return mergeSaaS(dbConfig, defaults, feature);
        } else {
            return mergeSelfHosted(dbConfig, defaults, feature);
        }
    }

    /**
     * SaaS: Tenant DB > Platform Env > YAML
     */
    private ResolvedLlmConfig mergeSaaS(LlmConfig dbConfig, FeatureDefaults defaults, String feature) {
        var builder = ResolvedLlmConfig.builder();
        Set<String> locked = new HashSet<>();

        // Provider
        if (dbConfig.getProvider() != null) {
            builder.provider(dbConfig.getProvider()).providerSource(ConfigSource.DATABASE);
        } else {
            builder.provider(defaults.provider).providerSource(ConfigSource.YAML_DEFAULT);
        }

        // Model
        if (dbConfig.getModel() != null) {
            builder.model(dbConfig.getModel()).modelSource(ConfigSource.DATABASE);
        } else {
            builder.model(defaults.model).modelSource(ConfigSource.YAML_DEFAULT);
        }

        // Temperature
        if (dbConfig.getTemperature() != null) {
            builder.temperature(dbConfig.getTemperature()).temperatureSource(ConfigSource.DATABASE);
        } else {
            builder.temperature(defaults.temperature).temperatureSource(ConfigSource.YAML_DEFAULT);
        }

        // MaxTokens
        builder.maxTokens(dbConfig.getMaxTokens() != null ? dbConfig.getMaxTokens() : defaults.maxTokens);

        // Base URL
        builder.baseUrl(dbConfig.getBaseUrl() != null ? dbConfig.getBaseUrl() : defaults.baseUrl);

        // API Key — tenant encrypted key takes precedence, then platform env
        String apiKey = null;
        ConfigSource apiKeySource = null;
        if (dbConfig.getEncryptedApiKey() != null && encryptionService.isConfigured()) {
            try {
                apiKey = encryptionService.decrypt(dbConfig.getEncryptedApiKey());
                apiKeySource = ConfigSource.DATABASE;
            } catch (Exception e) {
                log.warn("Failed to decrypt tenant API key for project {}: {}", dbConfig.getProjectId(), e.getMessage());
            }
        }
        if (apiKey == null) {
            apiKey = resolveApiKeyFromEnv(feature);
            apiKeySource = apiKey != null ? ConfigSource.ENVIRONMENT : null;
        }
        builder.apiKey(apiKey).apiKeySource(apiKeySource);

        builder.lockedFields(locked);
        return builder.build();
    }

    /**
     * Self-hosted: Env > DB > YAML. Env vars are hard locks.
     */
    private ResolvedLlmConfig mergeSelfHosted(LlmConfig dbConfig, FeatureDefaults defaults, String feature) {
        var builder = ResolvedLlmConfig.builder();
        Set<String> locked = new HashSet<>();
        String prefix = "PROMPTLY_LLM_" + feature.toUpperCase() + "_";

        // Provider
        String envProvider = springEnv.getProperty(prefix + "PROVIDER");
        if (envProvider != null) {
            builder.provider(envProvider).providerSource(ConfigSource.ENVIRONMENT);
            locked.add("provider");
        } else if (dbConfig.getProvider() != null) {
            builder.provider(dbConfig.getProvider()).providerSource(ConfigSource.DATABASE);
        } else {
            builder.provider(defaults.provider).providerSource(ConfigSource.YAML_DEFAULT);
        }

        // Model
        String envModel = springEnv.getProperty(prefix + "MODEL");
        if (envModel != null) {
            builder.model(envModel).modelSource(ConfigSource.ENVIRONMENT);
            locked.add("model");
        } else if (dbConfig.getModel() != null) {
            builder.model(dbConfig.getModel()).modelSource(ConfigSource.DATABASE);
        } else {
            builder.model(defaults.model).modelSource(ConfigSource.YAML_DEFAULT);
        }

        // Temperature
        String envTemp = springEnv.getProperty(prefix + "TEMPERATURE");
        if (envTemp != null) {
            builder.temperature(Double.parseDouble(envTemp)).temperatureSource(ConfigSource.ENVIRONMENT);
            locked.add("temperature");
        } else if (dbConfig.getTemperature() != null) {
            builder.temperature(dbConfig.getTemperature()).temperatureSource(ConfigSource.DATABASE);
        } else {
            builder.temperature(defaults.temperature).temperatureSource(ConfigSource.YAML_DEFAULT);
        }

        // MaxTokens
        builder.maxTokens(dbConfig.getMaxTokens() != null ? dbConfig.getMaxTokens() : defaults.maxTokens);

        // Base URL
        builder.baseUrl(dbConfig.getBaseUrl() != null ? dbConfig.getBaseUrl() : defaults.baseUrl);

        // API Key — always from env in self-hosted mode
        String apiKey = resolveApiKeyFromEnv(feature);
        builder.apiKey(apiKey).apiKeySource(apiKey != null ? ConfigSource.ENVIRONMENT : null);
        locked.add("apiKey");

        builder.lockedFields(locked);
        return builder.build();
    }

    private ResolvedLlmConfig buildFromDefaults(String feature) {
        var defaults = getFeatureDefaults(feature);
        String apiKey = resolveApiKeyFromEnv(feature);

        Set<String> locked = deploymentProps.isSelfHosted()
                ? Set.of("apiKey")
                : Set.of();

        return ResolvedLlmConfig.builder()
                .provider(defaults.provider)
                .model(defaults.model)
                .temperature(defaults.temperature)
                .maxTokens(defaults.maxTokens)
                .baseUrl(defaults.baseUrl)
                .apiKey(apiKey)
                .providerSource(ConfigSource.YAML_DEFAULT)
                .modelSource(ConfigSource.YAML_DEFAULT)
                .temperatureSource(ConfigSource.YAML_DEFAULT)
                .apiKeySource(apiKey != null ? ConfigSource.ENVIRONMENT : null)
                .lockedFields(locked)
                .build();
    }

    // ─── CRUD ─────────────────────────────────────────────────────

    /**
     * Returns all LLM configs for a project (all features).
     */
    @Override
    public Flux<LlmConfig> getConfigsByProject(String projectId) {
        return configRepo.findByProjectId(projectId);
    }

    /**
     * Saves or updates an LLM config. If an API key is provided in plaintext,
     * it will be encrypted before storage (SaaS mode only).
     */
    @Override
    public Mono<LlmConfig> saveConfig(SaveConfigCommand command) {
        String projectId = command.projectId();
        String feature = command.feature();
        String provider = command.provider();
        String model = command.model();
        Double temperature = command.temperature();
        Integer maxTokens = command.maxTokens();
        String baseUrl = command.baseUrl();
        String plaintextApiKey = command.plaintextApiKey();
        String updatedBy = command.updatedBy();

        // In self-hosted mode, reject API key storage attempts
        if (plaintextApiKey != null && deploymentProps.isSelfHosted()) {
            return Mono.error(new IllegalArgumentException(
                    "API keys cannot be stored in the database in self-hosted mode. " +
                    "Set the PROMPTLY_LLM_API_KEY environment variable instead."));
        }

        String encryptedKey = null;
        if (plaintextApiKey != null && !plaintextApiKey.isBlank()) {
            encryptedKey = encryptionService.encrypt(plaintextApiKey);
        }

        LlmConfig config = LlmConfig.builder()
                .projectId(projectId)
                .feature(feature)
                .provider(provider)
                .model(model)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .baseUrl(baseUrl)
                .encryptedApiKey(encryptedKey)
                .updatedAt(Instant.now())
                .updatedBy(updatedBy)
                .build();

        return configRepo.save(config);
    }

    /**
     * Resets a project's feature config back to platform defaults.
     */
    @Override
    public Mono<Void> resetConfig(String projectId, String feature) {
        return configRepo.deleteByProjectIdAndFeature(projectId, feature);
    }

    // ─── Helpers ──────────────────────────────────────────────────

    /**
     * Returns which fields are locked by environment variables for a given feature.
     */
    @Override
    public Set<String> getLockedFields(String feature) {
        if (deploymentProps.isSaas()) {
            return Set.of(); // SaaS: nothing locked, tenant controls all
        }
        Set<String> locked = new HashSet<>();
        String prefix = "PROMPTLY_LLM_" + feature.toUpperCase() + "_";
        if (springEnv.containsProperty(prefix + "PROVIDER")) locked.add("provider");
        if (springEnv.containsProperty(prefix + "MODEL")) locked.add("model");
        if (springEnv.containsProperty(prefix + "TEMPERATURE")) locked.add("temperature");
        locked.add("apiKey"); // Always locked in self-hosted
        return locked;
    }

    private String resolveApiKeyFromEnv(String feature) {
        String prefix = "PROMPTLY_LLM_" + feature.toUpperCase() + "_";
        // Feature-specific key first, then global
        String key = springEnv.getProperty(prefix + "API_KEY");
        if (key == null) {
            key = springEnv.getProperty("promptly.llm.api-key");
        }
        if (key == null) {
            key = yamlProps.getApiKey();
        }
        return key;
    }

    private FeatureDefaults getFeatureDefaults(String feature) {
        LlmProperties.FeatureConfig featureConfig = switch (feature.toLowerCase()) {
            case "scanner" -> yamlProps.getScanner();
            case "improver" -> yamlProps.getImprover();
            case "embedding" -> yamlProps.getEmbedding();
            default -> null;
        };

        return new FeatureDefaults(
                featureConfig != null && featureConfig.getProvider() != null
                        ? featureConfig.getProvider() : yamlProps.getProvider(),
                featureConfig != null && featureConfig.getModel() != null
                        ? featureConfig.getModel() : yamlProps.getModel(),
                featureConfig != null && featureConfig.getTemperature() != null
                        ? featureConfig.getTemperature() : yamlProps.getTemperature(),
                featureConfig != null && featureConfig.getMaxTokens() != null
                        ? featureConfig.getMaxTokens() : yamlProps.getMaxTokens(),
                yamlProps.getBaseUrl()
        );
    }

    private record FeatureDefaults(String provider, String model, double temperature, int maxTokens, String baseUrl) {}
}
