package com.promptly.search.infrastructure.embedding;

import com.promptly.search.application.port.out.EmbeddingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/**
 * Spring AI adapter for generating text embeddings.
 * Only active when an EmbeddingModel bean is available (i.e., an AI provider is configured).
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnBean(EmbeddingModel.class)
public class SpringAiEmbeddingAdapter implements EmbeddingPort {

    private final EmbeddingModel embeddingModel;

    @Override
    public float[] generateEmbedding(String text) {
        log.debug("Generating embedding for text of length {}", text.length());
        try {
            return embeddingModel.embed(text);
        } catch (Exception e) {
            log.warn("Embedding generation failed: {}", e.getMessage());
            // Return zero vector as fallback
            return new float[768]; // Gemini text-embedding-004 dimension
        }
    }

}
