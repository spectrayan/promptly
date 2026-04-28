package com.promptly.shared.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * Creates MongoDB indexes on application startup.
 * <p>
 * This replaces {@code auto-index-creation: true} with explicit, versioned index
 * definitions that are safe to run repeatedly (idempotent). Each index is named
 * explicitly so it can be tracked across deployments.
 * <p>
 * For a full migration framework (data transforms, rollbacks), consider
 * <a href="https://www.mongock.io/">Mongock</a> as a future evolution.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MongoIndexInitializer {

    private final ReactiveMongoTemplate mongoTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void ensureIndexes() {
        log.info("Ensuring MongoDB indexes...");
        Flux.merge(
                // ── Prompts ────────────────────────────────────────────────
                mongoTemplate.indexOps("prompts")
                        .ensureIndex(new Index()
                                .named("idx_project_name")
                                .on("projectId", Sort.Direction.ASC)
                                .on("name", Sort.Direction.ASC)
                                .unique()),
                mongoTemplate.indexOps("prompts")
                        .ensureIndex(new Index()
                                .named("idx_prompts_status")
                                .on("status", Sort.Direction.ASC)),
                mongoTemplate.indexOps("prompts")
                        .ensureIndex(new Index()
                                .named("idx_prompts_createdAt")
                                .on("createdAt", Sort.Direction.DESC)),

                // ── Projects ───────────────────────────────────────────────
                mongoTemplate.indexOps("projects")
                        .ensureIndex(new Index()
                                .named("idx_projects_name")
                                .on("name", Sort.Direction.ASC)
                                .unique()),
                mongoTemplate.indexOps("projects")
                        .ensureIndex(new Index()
                                .named("idx_projects_createdAt")
                                .on("createdAt", Sort.Direction.DESC)),

                // ── Project Members ────────────────────────────────────────
                mongoTemplate.indexOps("project_members")
                        .ensureIndex(new Index()
                                .named("idx_project_user")
                                .on("projectId", Sort.Direction.ASC)
                                .on("userId", Sort.Direction.ASC)
                                .unique()),

                // ── Users ──────────────────────────────────────────────────
                mongoTemplate.indexOps("users")
                        .ensureIndex(new Index()
                                .named("idx_users_email")
                                .on("email", Sort.Direction.ASC)
                                .unique()),

                // ── Workflows ──────────────────────────────────────────────
                mongoTemplate.indexOps("workflows")
                        .ensureIndex(new Index()
                                .named("idx_workflows_promptId")
                                .on("promptId", Sort.Direction.ASC)),
                mongoTemplate.indexOps("workflows")
                        .ensureIndex(new Index()
                                .named("idx_workflows_projectId_status")
                                .on("projectId", Sort.Direction.ASC)
                                .on("status", Sort.Direction.ASC)),
                mongoTemplate.indexOps("workflows")
                        .ensureIndex(new Index()
                                .named("idx_workflows_createdAt")
                                .on("createdAt", Sort.Direction.DESC)),

                // ── Audit Logs ─────────────────────────────────────────────
                mongoTemplate.indexOps("audit_logs")
                        .ensureIndex(new Index()
                                .named("idx_audit_resourceId")
                                .on("resourceId", Sort.Direction.ASC)),
                mongoTemplate.indexOps("audit_logs")
                        .ensureIndex(new Index()
                                .named("idx_audit_projectId_timestamp")
                                .on("projectId", Sort.Direction.ASC)
                                .on("timestamp", Sort.Direction.DESC)),

                // ── Notifications ──────────────────────────────────────────
                mongoTemplate.indexOps("notifications")
                        .ensureIndex(new Index()
                                .named("idx_notifications_userId_read")
                                .on("userId", Sort.Direction.ASC)
                                .on("read", Sort.Direction.ASC)),
                mongoTemplate.indexOps("notifications")
                        .ensureIndex(new Index()
                                .named("idx_notifications_createdAt")
                                .on("createdAt", Sort.Direction.DESC))
        )
        .doOnComplete(() -> log.info("All MongoDB indexes ensured successfully"))
        .doOnError(err -> log.error("Failed to create MongoDB indexes", err))
        .subscribe();
    }
}
