package com.promptly.llmconfig.application.port.out;

import com.promptly.llmconfig.domain.model.LlmConfig;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Outbound port for LLM config persistence.
 */
public interface LlmConfigRepository {

    Mono<LlmConfig> findByProjectIdAndFeature(String projectId, String feature);

    Flux<LlmConfig> findByProjectId(String projectId);

    Mono<LlmConfig> save(LlmConfig config);

    Mono<Void> deleteByProjectIdAndFeature(String projectId, String feature);
}
