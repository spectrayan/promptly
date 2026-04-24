// MongoDB Atlas Local — Vector Search Index Initialization
// This script creates the required indexes for the Promptly application.
// It runs automatically when the MongoDB container starts for the first time.

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
db.prompts.createIndex({ projectId: 1 });
db.prompts.createIndex({ activeEnvironment: 1 });
db.prompts.createIndex({ createdAt: -1 });

db.workflows.createIndex({ promptId: 1 });
db.workflows.createIndex({ status: 1 });
db.workflows.createIndex({ createdAt: -1 });

db.scan_results.createIndex({ promptId: 1, scannedAt: -1 });

db.audit_entries.createIndex({ resourceId: 1, timestamp: -1 });
db.audit_entries.createIndex({ actorUserId: 1 });
db.audit_entries.createIndex({ action: 1 });

print("✅ Regular indexes created on all collections");
