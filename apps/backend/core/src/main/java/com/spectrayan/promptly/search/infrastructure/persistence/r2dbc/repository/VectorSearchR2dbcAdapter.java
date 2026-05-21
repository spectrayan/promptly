package com.spectrayan.promptly.search.infrastructure.persistence.r2dbc.repository;

import com.spectrayan.promptly.search.application.port.in.SemanticSearchUseCase;
import com.spectrayan.promptly.search.application.port.out.VectorSearchPort;
import io.r2dbc.spi.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * R2DBC adapter implementing {@link VectorSearchPort} for PostgreSQL with pgvector.
 * <p>
 * This adapter is PostgreSQL-specific due to pgvector extension usage.
 * Other SQL dialects (H2, SQLite) do not support vector search.
 * Activated when {@code promptly.persistence.type=sql} AND pgvector is available.
 */
@Component
@ConditionalOnProperty(name = "promptly.persistence.type", havingValue = "sql")
@RequiredArgsConstructor
public class VectorSearchR2dbcAdapter implements VectorSearchPort {

    private final DatabaseClient databaseClient;

    @Override
    public Mono<Void> saveEmbedding(String promptId, float[] embedding) {
        String vectorStr = toVectorString(embedding);

        return databaseClient.sql("""
                        INSERT INTO prompt_embeddings (id, prompt_id, project_id, embedding, created_at, updated_at)
                        SELECT :id, :promptId, p.project_id, :embedding::vector, now(), now()
                        FROM prompts p WHERE p.id = :promptId
                        ON CONFLICT (prompt_id) DO UPDATE SET
                            embedding = EXCLUDED.embedding,
                            updated_at = now()
                        """)
                .bind("id", UUID.randomUUID().toString())
                .bind("promptId", promptId)
                .bind("embedding", vectorStr)
                .then();
    }

    @Override
    public Flux<SemanticSearchUseCase.SearchResult> searchByVector(float[] queryVector, int limit) {
        String vectorStr = toVectorString(queryVector);

        return databaseClient.sql("""
                        SELECT pe.prompt_id, p.name, p.description,
                               1 - (pe.embedding <=> :queryVector::vector) AS score
                        FROM prompt_embeddings pe
                        JOIN prompts p ON p.id = pe.prompt_id
                        ORDER BY pe.embedding <=> :queryVector::vector
                        LIMIT :limit
                        """)
                .bind("queryVector", vectorStr)
                .bind("limit", limit)
                .map(row -> new SemanticSearchUseCase.SearchResult(
                        row.get("prompt_id", String.class),
                        row.get("name", String.class),
                        row.get("description", String.class),
                        row.get("score", Double.class)
                ))
                .all();
    }

    @Override
    public Flux<SemanticSearchUseCase.SearchResult> findSimilarByPromptId(String promptId, int limit) {
        return databaseClient.sql("""
                        SELECT pe2.prompt_id, p.name, p.description,
                               1 - (pe2.embedding <=> pe1.embedding) AS score
                        FROM prompt_embeddings pe1
                        JOIN prompt_embeddings pe2 ON pe2.prompt_id != pe1.prompt_id
                        JOIN prompts p ON p.id = pe2.prompt_id
                        WHERE pe1.prompt_id = :promptId
                        ORDER BY pe2.embedding <=> pe1.embedding
                        LIMIT :limit
                        """)
                .bind("promptId", promptId)
                .bind("limit", limit)
                .map(row -> new SemanticSearchUseCase.SearchResult(
                        row.get("prompt_id", String.class),
                        row.get("name", String.class),
                        row.get("description", String.class),
                        row.get("score", Double.class)
                ))
                .all();
    }

    /**
     * Converts a float array into a pgvector-compatible string literal:
     * {@code [0.1,0.2,0.3,...]}
     */
    private String toVectorString(float[] vector) {
        return "[" + Arrays.stream(toBoxed(vector))
                .map(String::valueOf)
                .collect(Collectors.joining(",")) + "]";
    }

    private Float[] toBoxed(float[] array) {
        Float[] result = new Float[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i];
        }
        return result;
    }
}
