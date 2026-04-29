// MongoDB Atlas Local — Vector Search Index + Regular Index Initialization
// This script creates the required indexes for the Promptly application.
// It runs automatically when the MongoDB container starts for the first time.
//
// IMPORTANT: Index names MUST match seed-data/init.js and the Java
// MongoIndexInitializer to avoid IndexOptionsConflict errors.

db = db.getSiblingDB('promptly');

// ── Vector Search Index ────────────────────────────────────────────────
// Atlas Vector Search index on the prompts collection for semantic search.
// Gemini text-embedding-004 produces 768-dimensional vectors.
db.runCommand({
  createSearchIndex: "prompts",
  name: "prompt_embedding_index",
  type: "vectorSearch",
  definition: {
    fields: [
      {
        type: "vector",
        numDimensions: 768,
        path: "embedding",
        similarity: "cosine"
      },
      {
        type: "filter",
        path: "projectId"
      },
      {
        type: "filter",
        path: "activeEnvironment"
      }
    ]
  }
});

print("✅ Vector search index 'prompt_embedding_index' created on 'prompts' collection");

// ── Regular Indexes ───────────────────────────────────────────────────
// Names match seed-data/init.js and MongoIndexInitializer.java

db.prompts.createIndex({ projectId: 1, name: 1 }, { unique: true, name: "idx_project_name" });
db.prompts.createIndex({ status: 1 }, { name: "idx_prompts_status" });
db.prompts.createIndex({ createdAt: -1 }, { name: "idx_prompts_createdAt" });

db.projects.createIndex({ name: 1 }, { unique: true, name: "idx_project_name_unique" });
db.projects.createIndex({ createdAt: -1 }, { name: "idx_projects_createdAt" });

db.project_members.createIndex({ projectId: 1, userId: 1 }, { unique: true, name: "idx_project_user" });
db.project_members.createIndex({ userId: 1 }, { name: "idx_member_user" });

db.users.createIndex({ email: 1 }, { unique: true, name: "idx_user_email" });

db.workflows.createIndex({ promptId: 1 }, { name: "idx_workflow_prompt" });
db.workflows.createIndex({ projectId: 1, status: 1 }, { name: "idx_workflow_project_status" });
db.workflows.createIndex({ status: 1 }, { name: "idx_workflow_status" });
db.workflows.createIndex({ createdAt: -1 }, { name: "idx_workflows_createdAt" });

db.scan_results.createIndex({ promptId: 1, promptVersion: -1 }, { name: "idx_prompt_version" });
db.scan_results.createIndex({ projectId: 1, scannedAt: -1 }, { name: "idx_scan_project" });

db.audit_logs.createIndex({ resourceId: 1, timestamp: -1 }, { name: "idx_resource_timestamp" });
db.audit_logs.createIndex({ actorUserId: 1, timestamp: -1 }, { name: "idx_actor_timestamp" });
db.audit_logs.createIndex({ action: 1, timestamp: -1 }, { name: "idx_action_timestamp" });
db.audit_logs.createIndex({ projectId: 1, timestamp: -1 }, { name: "idx_audit_project" });

db.notifications.createIndex(
  { userId: 1, projectId: 1, read: 1, createdAt: -1 },
  { name: "idx_user_project_read_time" }
);
db.notifications.createIndex(
  { createdAt: 1 },
  { expireAfterSeconds: 2592000, name: "ttl_30d" }
);

db.notification_project_settings.createIndex(
  { projectId: 1 },
  { unique: true, name: "idx_notif_settings_project" }
);

print("✅ Regular indexes created on all collections");
