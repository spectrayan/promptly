// MongoDB Seed Data Loader for Promptly
// Loads JSON files from per-collection directories and creates indexes.
//
// Usage:
//   cat seed-data/init.js | docker exec -i <container> mongosh test
//
// Note: atlas-local + Spring Modulith uses the 'test' database by default

print("🌱 Seeding Promptly database...");

// ──────────────────────────────────────────────
// Helper: resolve seed-data dir relative to pwd
// mongosh doesn't have __dirname — we rely on
// the script being piped via `cat`, so we use
// the host-side path baked into the load script.
// ──────────────────────────────────────────────

const collections = [
  { name: "users",           file: "users/users.json" },
  { name: "projects",        file: "projects/projects.json" },
  { name: "project_members", file: "project_members/project_members.json" },
  { name: "prompts",         file: "prompts/prompts.json" },
  { name: "workflows",       file: "workflows/workflows.json" },
  { name: "scan_results",    file: "scan_results/scan_results.json" },
  { name: "audit_logs",      file: "audit_logs/audit_logs.json" }
];

// ──────────────────────────────────────────────
// Since mongosh inside Docker can't read host
// files, we use a shell wrapper (seed.sh) that
// builds a single JS payload. This init.js is
// kept as a thin orchestrator for when the JSON
// data is imported via mongoimport instead.
//
// For a single-command approach we provide
// seed-data/seed.sh which handles everything.
// ──────────────────────────────────────────────

// ──────────────────────────────────────────────
// INDEX DEFINITIONS
// ──────────────────────────────────────────────
const indexes = {
  prompts: [
    { keys: { projectId: 1, name: 1 }, options: { unique: true, name: "idx_project_name" } }
  ],
  workflows: [
    { keys: { promptId: 1 }, options: { name: "idx_workflow_prompt" } },
    { keys: { projectId: 1, status: 1 }, options: { name: "idx_workflow_project_status" } },
    { keys: { status: 1 }, options: { name: "idx_workflow_status" } }
  ],
  scan_results: [
    { keys: { promptId: 1, promptVersion: -1 }, options: { name: "idx_prompt_version" } },
    { keys: { projectId: 1, scannedAt: -1 }, options: { name: "idx_scan_project" } }
  ],
  audit_logs: [
    { keys: { resourceId: 1, timestamp: -1 }, options: { name: "idx_resource_timestamp" } },
    { keys: { actorUserId: 1, timestamp: -1 }, options: { name: "idx_actor_timestamp" } },
    { keys: { action: 1, timestamp: -1 }, options: { name: "idx_action_timestamp" } },
    { keys: { projectId: 1, timestamp: -1 }, options: { name: "idx_audit_project" } }
  ],
  users: [
    { keys: { email: 1 }, options: { unique: true, name: "idx_user_email" } }
  ],
  projects: [
    { keys: { name: 1 }, options: { unique: true, name: "idx_project_name_unique" } }
  ],
  project_members: [
    { keys: { projectId: 1, userId: 1 }, options: { unique: true, name: "idx_project_user" } },
    { keys: { userId: 1 }, options: { name: "idx_member_user" } }
  ]
};

// Create indexes
for (const [collName, idxList] of Object.entries(indexes)) {
  for (const idx of idxList) {
    db[collName].createIndex(idx.keys, idx.options);
    print("  📇 " + idx.options.name);
  }
}

print("");
print("🎉 Seed data loaded successfully!");
print("   Users:           " + db.users.countDocuments());
print("   Projects:        " + db.projects.countDocuments());
print("   Project Members: " + db.project_members.countDocuments());
print("   Prompts:         " + db.prompts.countDocuments());
print("   Workflows:       " + db.workflows.countDocuments());
print("   Scan Results:    " + db.scan_results.countDocuments());
print("   Audit Logs:      " + db.audit_logs.countDocuments());
