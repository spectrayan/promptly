package com.promptly.search.application.port.out;

/**
 * Outbound port for generating text embeddings.
 */
public interface EmbeddingPort {

    /**
     * Generates an embedding vector for the given text.
     * Runs on a virtual thread — blocking call is acceptable.
     */
    float[] generateEmbedding(String text);

}
