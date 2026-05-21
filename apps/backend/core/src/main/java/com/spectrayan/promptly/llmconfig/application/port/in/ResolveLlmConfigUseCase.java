package com.spectrayan.promptly.llmconfig.application.port.in;

import com.spectrayan.promptly.llmconfig.domain.model.ResolvedLlmConfig;
import reactor.core.publisher.Mono;

/**
 * Inbound port for resolving the effective LLM configuration.
 * <p>
 * Resolution follows a precedence chain that depends on deployment mode:
 * <ul>
 *   <li><b>SaaS</b>: Tenant DB &gt; Platform Env &gt; YAML</li>
 *   <li><b>Self-hosted</b>: Env &gt; DB &gt; YAML</li>
 * </ul>
 */
public interface ResolveLlmConfigUseCase {

    /**
     * Resolves the effective LLM configuration for a given project and feature.
     *
     * @param projectId the project scope
     * @param feature   the feature key (e.g. "scanner", "improver", "embedding", "global")
     * @return the resolved configuration with source tracking for each field
     */
    Mono<ResolvedLlmConfig> resolve(String projectId, String feature);
}
