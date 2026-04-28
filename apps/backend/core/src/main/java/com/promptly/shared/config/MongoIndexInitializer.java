package com.promptly.shared.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Creates MongoDB indexes on application startup.
 * <p>
 * Since {@code auto-index-creation} is disabled (correct for production),
 * this component ensures all required indexes exist programmatically.
 * Indexes are created idempotently — safe to run on every restart.
 * <p>
 * This replaces the need for Mongock or manual scripts for index management.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MongoIndexInitializer {

    private final ReactiveMongoTemplate mongoTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void ensureIndexes() {
        log.info("Ensuring MongoDB indexes...");

        Flux.concat(
                // users: unique email for login
                ensureIndex("users", "email", true),

                // projects: unique name
                ensureIndex("projects", "name", true),

                // prompts: lookup by projectId
                ensureIndex("prompts", "projectId", false),
                // prompts: lookup by name within project
                ensureCompoundIndex("prompts", List.of("projectId", "name"), false),

                // workflows: lookup by promptId
                ensureIndex("workflows", "promptId", false),
                // workflows: lookup by status
                ensureIndex("workflows", "status", false),

                // scan_results: lookup by promptId
                ensureIndex("scan_results", "promptId", false),

                // audit_logs: lookup by entityId and timestamp
                ensureIndex("audit_logs", "entityId", false),
                ensureIndex("audit_logs", "timestamp", false),

                // notifications: lookup by userId
                ensureIndex("notifications", "userId", false),

                // notification_project_settings: unique projectId
                ensureIndex("notification_project_settings", "projectId", true),

                // project_members: compound lookup
                ensureCompoundIndex("project_members", List.of("projectId", "userId"), true)
        )
        .then()
        .doOnSuccess(v -> log.info("MongoDB indexes ensured successfully"))
        .doOnError(e -> log.warn("Failed to create some indexes (non-fatal): {}", e.getMessage()))
        .onErrorResume(e -> Mono.empty())
        .subscribe();
    }

    private Mono<String> ensureIndex(String collection, String field, boolean unique) {
        Document keys = new Document(field, 1);
        Document indexOptions = new Document("background", true);
        if (unique) {
            indexOptions.append("unique", true);
        }
        String indexName = field + (unique ? "_unique" : "_idx");
        indexOptions.append("name", indexName);

        return mongoTemplate.getCollection(collection)
                .flatMap(c -> Mono.from(c.createIndex(keys, new com.mongodb.client.model.IndexOptions()
                        .background(true)
                        .unique(unique)
                        .name(indexName))))
                .doOnSuccess(name -> log.debug("Index ensured: {}.{}", collection, name))
                .onErrorResume(e -> {
                    log.debug("Index {}.{} already exists or skipped: {}", collection, indexName, e.getMessage());
                    return Mono.just(indexName);
                });
    }

    private Mono<String> ensureCompoundIndex(String collection, List<String> fields, boolean unique) {
        Document keys = new Document();
        fields.forEach(f -> keys.append(f, 1));
        String indexName = String.join("_", fields) + (unique ? "_unique" : "_idx");

        return mongoTemplate.getCollection(collection)
                .flatMap(c -> Mono.from(c.createIndex(keys, new com.mongodb.client.model.IndexOptions()
                        .background(true)
                        .unique(unique)
                        .name(indexName))))
                .doOnSuccess(name -> log.debug("Compound index ensured: {}.{}", collection, name))
                .onErrorResume(e -> {
                    log.debug("Index {}.{} already exists or skipped: {}", collection, indexName, e.getMessage());
                    return Mono.just(indexName);
                });
    }
}
