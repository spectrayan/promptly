package com.promptly.search.infrastructure.vectorsearch;

import com.promptly.prompt.PromptModuleApi;
import com.promptly.search.application.port.in.SemanticSearchUseCase;
import com.promptly.search.application.port.out.VectorSearchPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * MongoDB Atlas Vector Search adapter.
 * Uses {@code $vectorSearch} aggregation pipeline for real semantic search
 * against Gemini text-embedding-004 (768-dimensional) vectors.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AtlasVectorSearchAdapter implements VectorSearchPort {

    private static final String COLLECTION = "prompts";
    private static final String INDEX_NAME = "prompt_embedding_index";
    private static final int VECTOR_DIMENSIONS = 768;

    private final ReactiveMongoTemplate mongoTemplate;
    private final PromptModuleApi promptModuleApi;

    @Override
    public Mono<Void> saveEmbedding(String promptId, float[] embedding) {
        log.debug("Saving embedding for prompt: {} (dim={})", promptId, embedding.length);
        Query query = new Query(Criteria.where("_id").is(promptId));
        Update update = new Update().set("embedding", toDoubleList(embedding));
        return mongoTemplate.updateFirst(query, update, COLLECTION).then();
    }

    @Override
    public Flux<SemanticSearchUseCase.SearchResult> searchByVector(float[] queryVector, int limit) {
        log.debug("Atlas Vector Search: limit={}, dim={}", limit, queryVector.length);

        Document vectorSearchStage = new Document("$vectorSearch",
                new Document("index", INDEX_NAME)
                        .append("path", "embedding")
                        .append("queryVector", toDoubleList(queryVector))
                        .append("numCandidates", limit * 10)
                        .append("limit", limit)
        );

        Document projectStage = new Document("$project",
                new Document("name", 1)
                        .append("description", 1)
                        .append("score", new Document("$meta", "vectorSearchScore"))
        );

        List<Document> pipeline = List.of(vectorSearchStage, projectStage);

        return mongoTemplate.getCollection(COLLECTION)
                .flatMapMany(collection -> Flux.from(collection.aggregate(pipeline)))
                .map(doc -> new SemanticSearchUseCase.SearchResult(
                        doc.getObjectId("_id") != null ? doc.getObjectId("_id").toHexString() : doc.getString("_id"),
                        doc.getString("name"),
                        doc.getString("description"),
                        doc.getDouble("score") != null ? doc.getDouble("score") : 0.0
                ))
                .onErrorResume(e -> {
                    log.warn("Vector search failed (index may not be ready), falling back: {}", e.getMessage());
                    return fallbackSearch(limit);
                });
    }

    @Override
    public Flux<SemanticSearchUseCase.SearchResult> findSimilarByPromptId(String promptId, int limit) {
        log.debug("Finding similar prompts for: {}", promptId);

        // First get the embedding for this prompt, then search by it
        return mongoTemplate.findById(promptId, Document.class, COLLECTION)
                .flatMapMany(doc -> {
                    @SuppressWarnings("unchecked")
                    List<Double> embedding = doc.getList("embedding", Double.class);
                    if (embedding == null || embedding.isEmpty()) {
                        log.warn("No embedding found for prompt {}, using fallback", promptId);
                        return fallbackSimilar(promptId, limit);
                    }

                    float[] vector = new float[embedding.size()];
                    for (int i = 0; i < embedding.size(); i++) {
                        vector[i] = embedding.get(i).floatValue();
                    }
                    return searchByVector(vector, limit + 1)
                            .filter(r -> !r.promptId().equals(promptId))
                            .take(limit);
                })
                .switchIfEmpty(fallbackSimilar(promptId, limit));
    }

    // ── Fallback for when vector search index is not available ─────────

    private Flux<SemanticSearchUseCase.SearchResult> fallbackSearch(int limit) {
        return promptModuleApi.findAll()
                .take(limit)
                .map(prompt -> new SemanticSearchUseCase.SearchResult(
                        prompt.id(), prompt.name(), prompt.description(), 0.5
                ));
    }

    private Flux<SemanticSearchUseCase.SearchResult> fallbackSimilar(String promptId, int limit) {
        return promptModuleApi.findById(promptId)
                .flatMapMany(prompt -> promptModuleApi.findByProjectId(prompt.projectId()))
                .filter(p -> !p.id().equals(promptId))
                .take(limit)
                .map(prompt -> new SemanticSearchUseCase.SearchResult(
                        prompt.id(), prompt.name(), prompt.description(), 0.7
                ));
    }

    // ── Utility ──────────────────────────────────────────────────────

    private List<Double> toDoubleList(float[] floats) {
        Double[] doubles = new Double[floats.length];
        for (int i = 0; i < floats.length; i++) {
            doubles[i] = (double) floats[i];
        }
        return Arrays.asList(doubles);
    }

}
