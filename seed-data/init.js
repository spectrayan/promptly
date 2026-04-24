// MongoDB Seed Data for Promptly
// Run: Get-Content seed-data/init.js | docker exec -i promptly-mongodb mongosh test
// Note: atlas-local + Spring Modulith uses the 'test' database by default

print("🌱 Seeding Promptly database...");

// ──────────────────────────────────────────────
// USERS (for LOCAL auth mode)
// Password for all test users: "password123"
// Bcrypt hash of "password123" with 10 rounds
// ──────────────────────────────────────────────
db.users.drop();
const passwordHash = "$2a$10$xDPOLwFwRVqYypKiClF3oOmFk0e8r1CyeEL3PFjXqbCUG3mSmqmqS";
db.users.insertMany([
  {
    _id: "usr-001",
    email: "alice@promptly.ai",
    displayName: "Alice Johnson",
    passwordHash: passwordHash,
    avatarUrl: null,
    orgRole: "ORG_ADMIN",
    status: "ACTIVE",
    lastLoginAt: ISODate("2026-04-23T08:00:00Z"),
    createdAt: ISODate("2025-06-01T08:00:00Z"),
    updatedAt: ISODate("2026-04-23T08:00:00Z")
  },
  {
    _id: "usr-002",
    email: "bob@promptly.ai",
    displayName: "Bob Chen",
    passwordHash: passwordHash,
    avatarUrl: null,
    orgRole: "ORG_USER",
    status: "ACTIVE",
    lastLoginAt: ISODate("2026-04-22T14:30:00Z"),
    createdAt: ISODate("2025-07-15T10:00:00Z"),
    updatedAt: ISODate("2026-04-22T14:30:00Z")
  },
  {
    _id: "usr-003",
    email: "carol@promptly.ai",
    displayName: "Carol Diaz",
    passwordHash: passwordHash,
    avatarUrl: null,
    orgRole: "ORG_USER",
    status: "ACTIVE",
    lastLoginAt: ISODate("2026-04-21T09:15:00Z"),
    createdAt: ISODate("2025-10-01T11:00:00Z"),
    updatedAt: ISODate("2026-04-21T09:15:00Z")
  },
  {
    _id: "usr-004",
    email: "dave@promptly.ai",
    displayName: "Dave Kim",
    passwordHash: passwordHash,
    avatarUrl: null,
    orgRole: "ORG_USER",
    status: "ACTIVE",
    lastLoginAt: ISODate("2026-04-20T16:45:00Z"),
    createdAt: ISODate("2025-12-10T13:00:00Z"),
    updatedAt: ISODate("2026-04-20T16:45:00Z")
  }
]);
print("  ✅ Inserted " + db.users.countDocuments() + " users");

// ──────────────────────────────────────────────
// PROJECTS
// ──────────────────────────────────────────────
db.projects.drop();
db.projects.insertMany([
  {
    _id: "proj-001",
    name: "customer-ops",
    description: "Customer operations AI prompts for support automation",
    tags: ["support", "nlp", "classification"],
    createdBy: "usr-001",
    createdAt: ISODate("2025-06-15T08:00:00Z"),
    updatedAt: ISODate("2026-04-20T10:00:00Z")
  },
  {
    _id: "proj-002",
    name: "data-platform",
    description: "Data analytics and SQL generation prompts",
    tags: ["sql", "data", "analytics"],
    createdBy: "usr-002",
    createdAt: ISODate("2025-08-01T10:00:00Z"),
    updatedAt: ISODate("2026-04-10T08:15:00Z")
  },
  {
    _id: "proj-003",
    name: "dev-tools",
    description: "Developer productivity and code review tools",
    tags: ["code-review", "security", "devops"],
    createdBy: "usr-003",
    createdAt: ISODate("2025-12-01T11:00:00Z"),
    updatedAt: ISODate("2026-04-05T11:30:00Z")
  },
  {
    _id: "proj-004",
    name: "productivity",
    description: "Internal productivity tools — meeting notes, summaries",
    tags: ["meetings", "summarization"],
    createdBy: "usr-001",
    createdAt: ISODate("2026-03-01T09:00:00Z"),
    updatedAt: ISODate("2026-04-01T10:00:00Z")
  },
  {
    _id: "proj-005",
    name: "trust-safety",
    description: "Trust & safety — content moderation and policy compliance",
    tags: ["moderation", "compliance", "trust-safety"],
    createdBy: "usr-004",
    createdAt: ISODate("2025-05-01T08:00:00Z"),
    updatedAt: ISODate("2026-04-18T16:40:00Z")
  }
]);
print("  ✅ Inserted " + db.projects.countDocuments() + " projects");

// ──────────────────────────────────────────────
// PROJECT MEMBERS
// ──────────────────────────────────────────────
db.project_members.drop();
db.project_members.insertMany([
  // customer-ops: Alice(ADMIN), Bob(AUTHOR)
  { _id: "pm-001", projectId: "proj-001", userId: "usr-001", displayName: "Alice Johnson", email: "alice@promptly.ai", role: "ADMIN", joinedAt: ISODate("2025-06-15T08:00:00Z") },
  { _id: "pm-002", projectId: "proj-001", userId: "usr-002", displayName: "Bob Chen", email: "bob@promptly.ai", role: "AUTHOR", joinedAt: ISODate("2025-07-20T10:00:00Z") },

  // data-platform: Bob(ADMIN), Alice(REVIEWER)
  { _id: "pm-003", projectId: "proj-002", userId: "usr-002", displayName: "Bob Chen", email: "bob@promptly.ai", role: "ADMIN", joinedAt: ISODate("2025-08-01T10:00:00Z") },
  { _id: "pm-004", projectId: "proj-002", userId: "usr-001", displayName: "Alice Johnson", email: "alice@promptly.ai", role: "REVIEWER", joinedAt: ISODate("2025-08-10T08:00:00Z") },

  // dev-tools: Carol(ADMIN), Bob(REVIEWER)
  { _id: "pm-005", projectId: "proj-003", userId: "usr-003", displayName: "Carol Diaz", email: "carol@promptly.ai", role: "ADMIN", joinedAt: ISODate("2025-12-01T11:00:00Z") },
  { _id: "pm-006", projectId: "proj-003", userId: "usr-002", displayName: "Bob Chen", email: "bob@promptly.ai", role: "REVIEWER", joinedAt: ISODate("2025-12-15T10:00:00Z") },

  // productivity: Alice(ADMIN)
  { _id: "pm-007", projectId: "proj-004", userId: "usr-001", displayName: "Alice Johnson", email: "alice@promptly.ai", role: "ADMIN", joinedAt: ISODate("2026-03-01T09:00:00Z") },

  // trust-safety: Dave(ADMIN), Alice(APPROVER)
  { _id: "pm-008", projectId: "proj-005", userId: "usr-004", displayName: "Dave Kim", email: "dave@promptly.ai", role: "ADMIN", joinedAt: ISODate("2025-05-01T08:00:00Z") },
  { _id: "pm-009", projectId: "proj-005", userId: "usr-001", displayName: "Alice Johnson", email: "alice@promptly.ai", role: "APPROVER", joinedAt: ISODate("2025-06-01T08:00:00Z") }
]);
print("  ✅ Inserted " + db.project_members.countDocuments() + " project members");

// ──────────────────────────────────────────────
// PROMPTS
// ──────────────────────────────────────────────
db.prompts.drop();
db.prompts.insertMany([
  {
    _id: "p-001",
    name: "Customer Support Classifier",
    description: "Classifies customer support tickets into categories: billing, technical, account, general",
    projectId: "proj-001",
    contentFormat: "TEXT",
    tags: ["classification", "support", "nlp"],
    metadata: { model: "gemini-1.5-pro", temperature: 0.1, maxTokens: 50, systemContext: "Customer support automation" },
    currentVersion: 3,
    activeEnvironment: "STAGING",
    status: "APPROVED",
    versions: [
      { versionNumber: 1, content: "Classify the ticket into one of: BILLING, TECHNICAL, ACCOUNT, GENERAL.", changeMessage: "Initial version", createdBy: "alice@promptly.ai", createdAt: ISODate("2025-11-15T09:30:00Z") },
      { versionNumber: 2, content: "You are a customer support classifier (v2). Classify into: BILLING, TECHNICAL, ACCOUNT, GENERAL.", changeMessage: "Improved accuracy for technical tickets", createdBy: "bob@promptly.ai", createdAt: ISODate("2026-02-10T09:15:00Z") },
      { versionNumber: 3, content: "You are a customer support classifier. Given a support ticket, classify it into one of: BILLING, TECHNICAL, ACCOUNT, GENERAL.\n\nRules:\n- BILLING: payment issues, invoices, refunds, subscriptions\n- TECHNICAL: bugs, errors, performance, integrations\n- ACCOUNT: login, password, profile, permissions\n- GENERAL: everything else\n\nRespond with ONLY the category name.", changeMessage: "Added edge case handling for subscription queries", createdBy: "alice@promptly.ai", createdAt: ISODate("2026-03-20T14:22:00Z") }
    ],
    createdBy: "alice@promptly.ai",
    updatedBy: "alice@promptly.ai",
    createdAt: ISODate("2025-11-15T09:30:00Z"),
    updatedAt: ISODate("2026-03-20T14:22:00Z")
  },
  {
    _id: "p-002",
    name: "SQL Query Generator",
    description: "Generates safe, read-only SQL queries from natural language descriptions",
    projectId: "proj-002",
    contentFormat: "MARKDOWN",
    tags: ["sql", "data", "generation"],
    metadata: { model: "gemini-1.5-pro", temperature: 0.3, maxTokens: 2000, systemContext: "Data analytics platform" },
    currentVersion: 5,
    activeEnvironment: "PRODUCTION",
    status: "APPROVED",
    versions: [
      { versionNumber: 1, content: "Convert the user query to SQL.", changeMessage: "Initial version", createdBy: "bob@promptly.ai", createdAt: ISODate("2025-09-01T12:00:00Z") },
      { versionNumber: 5, content: "# SQL Query Generator\n\nYou are a SQL query generator. Convert natural language to safe, read-only PostgreSQL queries.\n\n## Rules\n- ONLY generate SELECT statements\n- NEVER generate INSERT, UPDATE, DELETE, DROP, or ALTER\n- Always include LIMIT 1000 unless user specifies\n- Use table aliases for readability", changeMessage: "Production-ready with full safety guardrails", createdBy: "bob@promptly.ai", createdAt: ISODate("2026-04-10T08:15:00Z") }
    ],
    createdBy: "bob@promptly.ai",
    updatedBy: "bob@promptly.ai",
    createdAt: ISODate("2025-09-01T12:00:00Z"),
    updatedAt: ISODate("2026-04-10T08:15:00Z")
  },
  {
    _id: "p-003",
    name: "Code Review Assistant",
    description: "Reviews pull requests for security vulnerabilities, best practices, and code quality",
    projectId: "proj-003",
    contentFormat: "JSON",
    tags: ["code-review", "security", "devops"],
    metadata: { model: "gemini-1.5-pro", temperature: 0.2, maxTokens: 4000, systemContext: "Developer tooling" },
    currentVersion: 2,
    activeEnvironment: "DEV",
    status: "DRAFT",
    versions: [
      { versionNumber: 1, content: "Review this code.", changeMessage: "Initial version", createdBy: "carol@promptly.ai", createdAt: ISODate("2026-01-10T16:45:00Z") },
      { versionNumber: 2, content: '{"system":"You are a senior code reviewer","rules":["Flag SQL injection risks","Identify missing null checks","Suggest performance improvements","Check for hardcoded secrets"]}', changeMessage: "Added structured output format", createdBy: "carol@promptly.ai", createdAt: ISODate("2026-04-05T11:30:00Z") }
    ],
    createdBy: "carol@promptly.ai",
    updatedBy: "carol@promptly.ai",
    createdAt: ISODate("2026-01-10T16:45:00Z"),
    updatedAt: ISODate("2026-04-05T11:30:00Z")
  },
  {
    _id: "p-004",
    name: "Meeting Summarizer",
    description: "Generates concise meeting summaries with action items and decisions",
    projectId: "proj-004",
    contentFormat: "TEXT",
    tags: ["summarization", "productivity", "meetings"],
    metadata: { model: "gemini-1.5-flash", temperature: 0.4, maxTokens: 1500, systemContext: "Internal productivity tools" },
    currentVersion: 1,
    activeEnvironment: "DEV",
    status: "DRAFT",
    versions: [
      { versionNumber: 1, content: "Summarize the following meeting transcript. Include: 1. Key Discussion Points 2. Decisions Made 3. Action Items 4. Open Questions", changeMessage: "Initial version", createdBy: "alice@promptly.ai", createdAt: ISODate("2026-04-01T10:00:00Z") }
    ],
    createdBy: "alice@promptly.ai",
    updatedBy: "alice@promptly.ai",
    createdAt: ISODate("2026-04-01T10:00:00Z"),
    updatedAt: ISODate("2026-04-01T10:00:00Z")
  },
  {
    _id: "p-005",
    name: "Content Moderation Filter",
    description: "Evaluates user-generated content for policy compliance and toxicity",
    projectId: "proj-005",
    contentFormat: "YAML",
    tags: ["moderation", "trust-safety", "compliance"],
    metadata: { model: "gemini-1.5-pro", temperature: 0.0, maxTokens: 500, systemContext: "Trust & safety platform" },
    currentVersion: 7,
    activeEnvironment: "PRODUCTION",
    status: "APPROVED",
    versions: [
      { versionNumber: 1, content: "Check if this content is safe.", changeMessage: "Initial version", createdBy: "dave@promptly.ai", createdAt: ISODate("2025-06-20T08:00:00Z") },
      { versionNumber: 7, content: "role: content_moderator\nversion: 7\nthresholds:\n  toxicity: 0.7\n  spam: 0.8\n  pii_detection: true", changeMessage: "Added misinformation detection category", createdBy: "dave@promptly.ai", createdAt: ISODate("2026-04-18T16:40:00Z") }
    ],
    createdBy: "dave@promptly.ai",
    updatedBy: "dave@promptly.ai",
    createdAt: ISODate("2025-06-20T08:00:00Z"),
    updatedAt: ISODate("2026-04-18T16:40:00Z")
  }
]);
print("  ✅ Inserted " + db.prompts.countDocuments() + " prompts");

// ──────────────────────────────────────────────
// WORKFLOWS
// ──────────────────────────────────────────────
db.workflows.drop();
db.workflows.insertMany([
  {
    _id: "wf-001",
    promptId: "p-002",
    promptVersion: 5,
    type: "PROMOTION",
    status: "PENDING",
    currentStep: 0,
    sourceEnvironment: "STAGING",
    targetEnvironment: "PRODUCTION",
    requestedBy: "bob@promptly.ai",
    steps: [
      { step: 0, role: "REVIEWER", assignedTo: "alice@promptly.ai", action: null, comment: null, actedAt: null }
    ],
    createdAt: ISODate("2026-04-20T10:00:00Z"),
    updatedAt: ISODate("2026-04-20T10:00:00Z")
  },
  {
    _id: "wf-002",
    promptId: "p-005",
    promptVersion: 7,
    type: "PROMOTION",
    status: "APPROVED",
    currentStep: 1,
    sourceEnvironment: "STAGING",
    targetEnvironment: "PRODUCTION",
    requestedBy: "dave@promptly.ai",
    steps: [
      { step: 0, role: "REVIEWER", assignedTo: "alice@promptly.ai", action: "APPROVE", comment: "Looks good, all tests pass", actedAt: ISODate("2026-04-19T16:30:00Z") }
    ],
    createdAt: ISODate("2026-04-18T14:00:00Z"),
    updatedAt: ISODate("2026-04-19T16:30:00Z")
  },
  {
    _id: "wf-003",
    promptId: "p-001",
    promptVersion: 3,
    type: "PROMOTION",
    status: "IN_REVIEW",
    currentStep: 0,
    sourceEnvironment: "DEV",
    targetEnvironment: "STAGING",
    requestedBy: "alice@promptly.ai",
    steps: [
      { step: 0, role: "REVIEWER", assignedTo: "bob@promptly.ai", action: null, comment: null, actedAt: null }
    ],
    createdAt: ISODate("2026-04-22T09:00:00Z"),
    updatedAt: ISODate("2026-04-22T11:30:00Z")
  },
  {
    _id: "wf-004",
    promptId: "p-003",
    promptVersion: 2,
    type: "PROMOTION",
    status: "REJECTED",
    currentStep: 1,
    sourceEnvironment: "DEV",
    targetEnvironment: "STAGING",
    requestedBy: "carol@promptly.ai",
    steps: [
      { step: 0, role: "REVIEWER", assignedTo: "bob@promptly.ai", action: "REJECT", comment: "Missing test coverage for edge cases", actedAt: ISODate("2026-04-16T10:00:00Z") }
    ],
    createdAt: ISODate("2026-04-15T08:00:00Z"),
    updatedAt: ISODate("2026-04-16T10:00:00Z")
  }
]);
print("  ✅ Inserted " + db.workflows.countDocuments() + " workflows");

// ──────────────────────────────────────────────
// SCAN RESULTS
// ──────────────────────────────────────────────
db.scan_results.drop();
db.scan_results.insertMany([
  {
    _id: "scan-001",
    promptId: "p-001",
    promptVersion: 3,
    overallScore: 0.95,
    status: "pass",
    llmProvider: "google",
    llmModel: "gemini-1.5-pro",
    scannedBy: "system",
    scannedAt: ISODate("2026-04-22T08:00:00Z"),
    findings: [],
    createdAt: ISODate("2026-04-22T08:00:00Z")
  },
  {
    _id: "scan-002",
    promptId: "p-002",
    promptVersion: 5,
    overallScore: 0.90,
    status: "pass",
    llmProvider: "google",
    llmModel: "gemini-1.5-pro",
    scannedBy: "system",
    scannedAt: ISODate("2026-04-20T10:30:00Z"),
    findings: [
      { type: "info", severity: "low", title: "PII Detection", description: "No PII patterns found in prompt content", remediation: null }
    ],
    createdAt: ISODate("2026-04-20T10:30:00Z")
  },
  {
    _id: "scan-003",
    promptId: "p-003",
    promptVersion: 2,
    overallScore: 0.65,
    status: "warn",
    llmProvider: "google",
    llmModel: "gemini-1.5-pro",
    scannedBy: "system",
    scannedAt: ISODate("2026-04-19T14:00:00Z"),
    findings: [
      { type: "security", severity: "medium", title: "Prompt Injection Vector", description: "Output format specification could be manipulated to inject instructions", remediation: "Add output format validation and sandboxing" }
    ],
    createdAt: ISODate("2026-04-19T14:00:00Z")
  },
  {
    _id: "scan-004",
    promptId: "p-005",
    promptVersion: 7,
    overallScore: 0.98,
    status: "pass",
    llmProvider: "google",
    llmModel: "gemini-1.5-pro",
    scannedBy: "system",
    scannedAt: ISODate("2026-04-18T16:00:00Z"),
    findings: [],
    createdAt: ISODate("2026-04-18T16:00:00Z")
  }
]);
print("  ✅ Inserted " + db.scan_results.countDocuments() + " scan results");

// ──────────────────────────────────────────────
// AUDIT LOGS
// ──────────────────────────────────────────────
db.audit_logs.drop();
db.audit_logs.insertMany([
  { _id: "al-001", action: "prompt.created", resourceType: "PROMPT", resourceId: "p-001", resourceVersion: 1, actorUserId: "alice@promptly.ai", actorEmail: "alice@promptly.ai", actorRole: "ADMIN", details: { name: "Customer Support Classifier" }, timestamp: ISODate("2025-11-15T09:30:00Z"), createdAt: ISODate("2025-11-15T09:30:00Z") },
  { _id: "al-002", action: "prompt.updated", resourceType: "PROMPT", resourceId: "p-001", resourceVersion: 2, actorUserId: "bob@promptly.ai", actorEmail: "bob@promptly.ai", actorRole: "DEVELOPER", details: { changeMessage: "Improved accuracy for technical tickets" }, timestamp: ISODate("2026-02-10T09:15:00Z"), createdAt: ISODate("2026-02-10T09:15:00Z") },
  { _id: "al-003", action: "prompt.updated", resourceType: "PROMPT", resourceId: "p-001", resourceVersion: 3, actorUserId: "alice@promptly.ai", actorEmail: "alice@promptly.ai", actorRole: "ADMIN", details: { changeMessage: "Added edge case handling" }, timestamp: ISODate("2026-03-20T14:22:00Z"), createdAt: ISODate("2026-03-20T14:22:00Z") },
  { _id: "al-004", action: "prompt.created", resourceType: "PROMPT", resourceId: "p-002", resourceVersion: 1, actorUserId: "bob@promptly.ai", actorEmail: "bob@promptly.ai", actorRole: "DEVELOPER", details: { name: "SQL Query Generator" }, timestamp: ISODate("2025-09-01T12:00:00Z"), createdAt: ISODate("2025-09-01T12:00:00Z") },
  { _id: "al-005", action: "prompt.created", resourceType: "PROMPT", resourceId: "p-004", resourceVersion: 1, actorUserId: "alice@promptly.ai", actorEmail: "alice@promptly.ai", actorRole: "ADMIN", details: { name: "Meeting Summarizer" }, timestamp: ISODate("2026-04-01T10:00:00Z"), createdAt: ISODate("2026-04-01T10:00:00Z") },
  { _id: "al-006", action: "workflow.created", resourceType: "WORKFLOW", resourceId: "wf-001", actorUserId: "bob@promptly.ai", actorEmail: "bob@promptly.ai", actorRole: "DEVELOPER", details: { promptId: "p-002", targetEnvironment: "PRODUCTION" }, timestamp: ISODate("2026-04-20T10:00:00Z"), createdAt: ISODate("2026-04-20T10:00:00Z") },
  { _id: "al-007", action: "workflow.approved", resourceType: "WORKFLOW", resourceId: "wf-002", actorUserId: "alice@promptly.ai", actorEmail: "alice@promptly.ai", actorRole: "ADMIN", details: { promptId: "p-005", comment: "Looks good, all tests pass" }, timestamp: ISODate("2026-04-19T16:30:00Z"), createdAt: ISODate("2026-04-19T16:30:00Z") },
  { _id: "al-008", action: "workflow.rejected", resourceType: "WORKFLOW", resourceId: "wf-004", actorUserId: "bob@promptly.ai", actorEmail: "bob@promptly.ai", actorRole: "DEVELOPER", details: { promptId: "p-003", comment: "Missing test coverage" }, timestamp: ISODate("2026-04-16T10:00:00Z"), createdAt: ISODate("2026-04-16T10:00:00Z") },
  { _id: "al-009", action: "scan.completed", resourceType: "SCAN", resourceId: "scan-002", actorUserId: "system", actorEmail: null, actorRole: "SYSTEM", details: { promptId: "p-002", status: "pass" }, timestamp: ISODate("2026-04-20T10:30:00Z"), createdAt: ISODate("2026-04-20T10:30:00Z") },
  { _id: "al-010", action: "scan.completed", resourceType: "SCAN", resourceId: "scan-003", actorUserId: "system", actorEmail: null, actorRole: "SYSTEM", details: { promptId: "p-003", status: "warn" }, timestamp: ISODate("2026-04-19T14:00:00Z"), createdAt: ISODate("2026-04-19T14:00:00Z") }
]);
print("  ✅ Inserted " + db.audit_logs.countDocuments() + " audit log entries");

// ──────────────────────────────────────────────
// CREATE INDEXES
// ──────────────────────────────────────────────
db.prompts.createIndex({ projectId: 1, name: 1 }, { unique: true, name: "idx_project_name" });
db.workflows.createIndex({ promptId: 1 }, { name: "idx_workflow_prompt" });
db.workflows.createIndex({ status: 1 }, { name: "idx_workflow_status" });
db.scan_results.createIndex({ promptId: 1, promptVersion: -1 }, { name: "idx_prompt_version" });
db.audit_logs.createIndex({ resourceId: 1, timestamp: -1 }, { name: "idx_resource_timestamp" });
db.audit_logs.createIndex({ actorUserId: 1, timestamp: -1 }, { name: "idx_actor_timestamp" });
db.audit_logs.createIndex({ action: 1, timestamp: -1 }, { name: "idx_action_timestamp" });
db.users.createIndex({ email: 1 }, { unique: true, name: "idx_user_email" });
db.projects.createIndex({ name: 1 }, { unique: true, name: "idx_project_name_unique" });
db.project_members.createIndex({ projectId: 1, userId: 1 }, { unique: true, name: "idx_project_user" });
db.project_members.createIndex({ userId: 1 }, { name: "idx_member_user" });

print("");
print("🎉 Seed data loaded successfully!");
print("   Users:           " + db.users.countDocuments());
print("   Projects:        " + db.projects.countDocuments());
print("   Project Members: " + db.project_members.countDocuments());
print("   Prompts:         " + db.prompts.countDocuments());
print("   Workflows:       " + db.workflows.countDocuments());
print("   Scan Results:    " + db.scan_results.countDocuments());
print("   Audit Logs:      " + db.audit_logs.countDocuments());
