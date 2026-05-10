package com.spectrayan.promptly.shared.infrastructure.persistence.mongo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Creates MongoDB indexes on application startup.
 * <p>
 * This replaces {@code auto-index-creation: true} with explicit, versioned index
 * definitions that are safe to run repeatedly (idempotent). Each index is named
 * explicitly so it can be tracked across deployments.
 * <p>
 * <strong>Index names MUST match the seed script ({@code seed-data/init.js})</strong>
 * to avoid {@code IndexOptionsConflict} errors when both run against the same DB.
 * <p>
 * For a full migration framework (data transforms, rollbacks), consider
 * <a href="https://www.mongock.io/">Mongock</a> as a future evolution.
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "promptly.persistence.type", havingValue = "mongo", matchIfMissing = true)
@RequiredArgsConstructor
public class MongoIndexInitializer {

    private final ReactiveMongoTemplate mongoTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void ensureIndexes() {
        log.info("Ensuring MongoDB indexes...");
        Flux.merge(
                // ── Prompts ────────────────────────────────────────────────
                ensureIndex("prompts",
                        new Index().named("idx_project_name")
                                .on("projectId", Sort.Direction.ASC)
                                .on("name", Sort.Direction.ASC)
                                .unique()),
                ensureIndex("prompts",
                        new Index().named("idx_prompts_status")
                                .on("status", Sort.Direction.ASC)),
                ensureIndex("prompts",
                        new Index().named("idx_prompts_createdAt")
                                .on("createdAt", Sort.Direction.DESC)),

                // ── Prompt History ─────────────────────────────────────────
                ensureIndex("prompt_history",
                        new Index().named("idx_prompt_history_prompt_version")
                                .on("promptId", Sort.Direction.ASC)
                                .on("versionNumber", Sort.Direction.DESC)
                                .unique()),
                ensureIndex("prompt_history",
                        new Index().named("idx_prompt_history_createdAt")
                                .on("createdAt", Sort.Direction.DESC)),

                // ── Projects ───────────────────────────────────────────────
                ensureIndex("projects",
                        new Index().named("idx_project_name_unique")
                                .on("name", Sort.Direction.ASC)
                                .unique()),
                ensureIndex("projects",
                        new Index().named("idx_projects_createdAt")
                                .on("createdAt", Sort.Direction.DESC)),

                // ── Project Members ────────────────────────────────────────
                ensureIndex("project_members",
                        new Index().named("idx_project_user")
                                .on("projectId", Sort.Direction.ASC)
                                .on("userId", Sort.Direction.ASC)
                                .unique()),
                ensureIndex("project_members",
                        new Index().named("idx_member_user")
                                .on("userId", Sort.Direction.ASC)),

                // ── Users ──────────────────────────────────────────────────
                ensureIndex("users",
                        new Index().named("idx_user_email")
                                .on("email", Sort.Direction.ASC)
                                .unique()),

                // ── Workflows ──────────────────────────────────────────────
                ensureIndex("workflows",
                        new Index().named("idx_workflow_prompt")
                                .on("promptId", Sort.Direction.ASC)),
                ensureIndex("workflows",
                        new Index().named("idx_workflow_project_status")
                                .on("projectId", Sort.Direction.ASC)
                                .on("status", Sort.Direction.ASC)),
                ensureIndex("workflows",
                        new Index().named("idx_workflow_status")
                                .on("status", Sort.Direction.ASC)),
                ensureIndex("workflows",
                        new Index().named("idx_workflows_createdAt")
                                .on("createdAt", Sort.Direction.DESC)),

                // ── Workflow Steps ─────────────────────────────────────────
                ensureIndex("workflow_steps",
                        new Index().named("idx_wfstep_workflow_step")
                                .on("workflowId", Sort.Direction.ASC)
                                .on("step", Sort.Direction.ASC)
                                .unique()),
                ensureIndex("workflow_steps",
                        new Index().named("idx_wfstep_workflow")
                                .on("workflowId", Sort.Direction.ASC)),

                // ── Scan Results ───────────────────────────────────────────
                ensureIndex("scan_results",
                        new Index().named("idx_prompt_version")
                                .on("promptId", Sort.Direction.ASC)
                                .on("promptVersion", Sort.Direction.DESC)),
                ensureIndex("scan_results",
                        new Index().named("idx_scan_project")
                                .on("projectId", Sort.Direction.ASC)
                                .on("scannedAt", Sort.Direction.DESC)),

                // ── Audit Logs ─────────────────────────────────────────────
                ensureIndex("audit_logs",
                        new Index().named("idx_resource_timestamp")
                                .on("resourceId", Sort.Direction.ASC)
                                .on("timestamp", Sort.Direction.DESC)),
                ensureIndex("audit_logs",
                        new Index().named("idx_actor_timestamp")
                                .on("actorUserId", Sort.Direction.ASC)
                                .on("timestamp", Sort.Direction.DESC)),
                ensureIndex("audit_logs",
                        new Index().named("idx_action_timestamp")
                                .on("action", Sort.Direction.ASC)
                                .on("timestamp", Sort.Direction.DESC)),
                ensureIndex("audit_logs",
                        new Index().named("idx_audit_project")
                                .on("projectId", Sort.Direction.ASC)
                                .on("timestamp", Sort.Direction.DESC)),

                // ── Notifications ──────────────────────────────────────────
                ensureIndex("notifications",
                        new Index().named("idx_user_project_read_time")
                                .on("userId", Sort.Direction.ASC)
                                .on("projectId", Sort.Direction.ASC)
                                .on("read", Sort.Direction.ASC)
                                .on("createdAt", Sort.Direction.DESC)),

                // ── Notification Project Settings ──────────────────────────
                ensureIndex("notification_project_settings",
                        new Index().named("idx_notif_settings_project")
                                .on("projectId", Sort.Direction.ASC)
                                .unique())
        )
        .doOnComplete(() -> log.info("All MongoDB indexes ensured successfully"))
        .doOnError(err -> log.error("Failed to create MongoDB indexes", err))
        .subscribe();
    }

    /**
     * Create a single index, swallowing conflicts (e.g. if the index already
     * exists with an identical definition). This makes the initializer truly
     * idempotent and safe to run alongside the seed script.
     */
    private Mono<String> ensureIndex(String collection, Index index) {
        return mongoTemplate.indexOps(collection)
                .ensureIndex(index)
                .doOnSuccess(name -> log.debug("Index ensured: {}.{}", collection, name))
                .onErrorResume(e -> {
                    log.warn("Index conflict on {}: {} (non-fatal, skipping)", collection, e.getMessage());
                    return Mono.empty();
                });
    }
}
