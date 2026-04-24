package com.promptly.search.infrastructure.embedding;

import com.promptly.search.application.port.out.EmbeddingPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * No-op embedding adapter used when no AI provider is configured (dev mode).
 * Returns a zero vector so the application can start without an embedding service.
 */
@Slf4j
@Component
@ConditionalOnMissingBean(EmbeddingModel.class)
public class NoOpEmbeddingAdapter implements EmbeddingPort {

    @Override
    public float[] generateEmbedding(String text) {
        log.debug("NoOp embedding — AI provider not configured. Returning zero vector.");
        return new float[768];
    }
}
