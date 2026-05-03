-- ════════════════════════════════════════════════════════════════════
-- V1__baseline_schema.sql
-- Baseline schema for Promptly — PostgreSQL
-- Mirrors the MongoDB document model with relational normalization.
-- ════════════════════════════════════════════════════════════════════

-- ── Extension: pgvector for semantic search ──────────────────────
CREATE EXTENSION IF NOT EXISTS vector;

-- ══════════════════════════════════════════════════════════════════
-- Auth Module
-- ══════════════════════════════════════════════════════════════════

CREATE TABLE users (
    id              VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255),
    display_name    VARCHAR(200),
    avatar_url      VARCHAR(500),
    org_role        VARCHAR(20)  NOT NULL DEFAULT 'VIEWER',
    status          VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    last_login_at   TIMESTAMPTZ,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- ══════════════════════════════════════════════════════════════════
-- Project Module
-- ══════════════════════════════════════════════════════════════════

CREATE TABLE projects (
    id              VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::text,
    name            VARCHAR(200) NOT NULL,
    description     VARCHAR(1000),
    tags            JSONB        DEFAULT '[]',
    created_by      VARCHAR(100),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE project_members (
    id              VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::text,
    project_id      VARCHAR(36)  NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    user_id         VARCHAR(100) NOT NULL,
    role            VARCHAR(20)  NOT NULL DEFAULT 'VIEWER',
    added_by        VARCHAR(100),
    added_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (project_id, user_id)
);

CREATE INDEX idx_project_members_project ON project_members(project_id);
CREATE INDEX idx_project_members_user    ON project_members(user_id);

-- ══════════════════════════════════════════════════════════════════
-- Prompt Module
-- ══════════════════════════════════════════════════════════════════

CREATE TABLE prompts (
    id                       VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::text,
    name                     VARCHAR(200) NOT NULL,
    description              VARCHAR(1000),
    project_id               VARCHAR(100) NOT NULL,
    content_format           VARCHAR(20),
    tags                     JSONB        DEFAULT '[]',
    metadata_model           VARCHAR(100),
    metadata_temperature     DOUBLE PRECISION,
    metadata_max_tokens      INTEGER,
    metadata_system_context  TEXT,
    current_version          INTEGER      NOT NULL DEFAULT 0,
    status                   VARCHAR(20)  NOT NULL DEFAULT 'DRAFT',
    version                  BIGINT       NOT NULL DEFAULT 0,  -- optimistic locking
    created_at               TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at               TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_by               VARCHAR(100),
    updated_by               VARCHAR(100),
    UNIQUE (project_id, name)
);

CREATE INDEX idx_prompts_project ON prompts(project_id);
CREATE INDEX idx_prompts_status  ON prompts(project_id, status);

CREATE TABLE prompt_history (
    id              VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::text,
    prompt_id       VARCHAR(36)  NOT NULL REFERENCES prompts(id) ON DELETE CASCADE,
    version_number  INTEGER      NOT NULL,
    content         TEXT         NOT NULL,
    change_message  VARCHAR(500),
    created_by      VARCHAR(100),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (prompt_id, version_number)
);

CREATE INDEX idx_prompt_history_prompt ON prompt_history(prompt_id, version_number DESC);

-- ══════════════════════════════════════════════════════════════════
-- Workflow Module
-- ══════════════════════════════════════════════════════════════════

CREATE TABLE workflows (
    id              VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::text,
    project_id      VARCHAR(100) NOT NULL,
    prompt_id       VARCHAR(36)  NOT NULL,
    prompt_version  INTEGER      NOT NULL,
    type            VARCHAR(50)  NOT NULL,
    status          VARCHAR(20)  NOT NULL,
    current_step    INTEGER      NOT NULL DEFAULT 0,
    requested_by    VARCHAR(100) NOT NULL,
    version         BIGINT       NOT NULL DEFAULT 0,  -- optimistic locking
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_workflows_project ON workflows(project_id);
CREATE INDEX idx_workflows_prompt  ON workflows(prompt_id);
CREATE INDEX idx_workflows_status  ON workflows(status);

CREATE TABLE workflow_steps (
    id              VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::text,
    workflow_id     VARCHAR(36)  NOT NULL REFERENCES workflows(id) ON DELETE CASCADE,
    step            INTEGER      NOT NULL,
    role            VARCHAR(50),
    assigned_to     VARCHAR(100),
    action          VARCHAR(20),
    comment         TEXT,
    acted_at        TIMESTAMPTZ,
    UNIQUE (workflow_id, step)
);

CREATE INDEX idx_workflow_steps_workflow ON workflow_steps(workflow_id, step);

-- ══════════════════════════════════════════════════════════════════
-- Audit Module
-- ══════════════════════════════════════════════════════════════════

CREATE TABLE audit_logs (
    id                VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::text,
    project_id        VARCHAR(100),
    action            VARCHAR(50)  NOT NULL,
    resource_type     VARCHAR(50)  NOT NULL,
    resource_id       VARCHAR(100) NOT NULL,
    resource_version  INTEGER,
    actor_user_id     VARCHAR(100) NOT NULL,
    actor_email       VARCHAR(255),
    actor_role        VARCHAR(50),
    details           JSONB,
    timestamp         TIMESTAMPTZ  NOT NULL DEFAULT now(),
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_audit_resource ON audit_logs(resource_type, resource_id);
CREATE INDEX idx_audit_project  ON audit_logs(project_id, timestamp DESC);
CREATE INDEX idx_audit_actor    ON audit_logs(actor_user_id, timestamp DESC);
CREATE INDEX idx_audit_action   ON audit_logs(action);

-- ══════════════════════════════════════════════════════════════════
-- Scanner Module
-- ══════════════════════════════════════════════════════════════════

CREATE TABLE scan_results (
    id              VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::text,
    project_id      VARCHAR(100) NOT NULL,
    prompt_id       VARCHAR(36)  NOT NULL,
    prompt_version  INTEGER      NOT NULL,
    overall_score   DOUBLE PRECISION,
    status          VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    llm_provider    VARCHAR(50),
    llm_model       VARCHAR(100),
    scanned_by      VARCHAR(100),
    findings        JSONB        DEFAULT '[]',
    scanned_at      TIMESTAMPTZ,
    version         BIGINT       NOT NULL DEFAULT 0,  -- optimistic locking
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_scan_results_prompt  ON scan_results(prompt_id, prompt_version);
CREATE INDEX idx_scan_results_project ON scan_results(project_id);

-- ══════════════════════════════════════════════════════════════════
-- Notification Module
-- ══════════════════════════════════════════════════════════════════

CREATE TABLE notifications (
    id              VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::text,
    user_id         VARCHAR(100) NOT NULL,
    project_id      VARCHAR(100),
    type            VARCHAR(50)  NOT NULL,
    title           VARCHAR(200) NOT NULL,
    message         TEXT,
    icon            VARCHAR(50),
    payload         JSONB,
    is_read         BOOLEAN      NOT NULL DEFAULT false,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_notifications_user    ON notifications(user_id, project_id, is_read, created_at DESC);

CREATE TABLE notification_preferences (
    id              VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::text,
    user_id         VARCHAR(100) NOT NULL,
    project_id      VARCHAR(100) NOT NULL,
    muted_events    JSONB        DEFAULT '[]',
    in_app_enabled  BOOLEAN      NOT NULL DEFAULT true,
    email_enabled   BOOLEAN      NOT NULL DEFAULT true,
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (user_id, project_id)
);

CREATE TABLE project_notification_settings (
    id              VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::text,
    project_id      VARCHAR(100) NOT NULL UNIQUE,
    enabled_events  JSONB        DEFAULT '[]',
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- ══════════════════════════════════════════════════════════════════
-- LLM Config Module
-- ══════════════════════════════════════════════════════════════════

CREATE TABLE llm_configs (
    id                  VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::text,
    project_id          VARCHAR(100) NOT NULL,
    feature             VARCHAR(50)  NOT NULL DEFAULT 'global',
    provider            VARCHAR(50)  NOT NULL,
    model               VARCHAR(100) NOT NULL,
    encrypted_api_key   TEXT,
    base_url            VARCHAR(500),
    temperature         DOUBLE PRECISION,
    max_tokens          INTEGER,
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_by          VARCHAR(100),
    UNIQUE (project_id, feature)
);

-- ══════════════════════════════════════════════════════════════════
-- Search Module (pgvector)
-- ══════════════════════════════════════════════════════════════════

CREATE TABLE prompt_embeddings (
    id              VARCHAR(36)  PRIMARY KEY DEFAULT gen_random_uuid()::text,
    prompt_id       VARCHAR(36)  NOT NULL UNIQUE,
    project_id      VARCHAR(100) NOT NULL,
    embedding       vector(768),  -- dimension matches text-embedding-004
    content_hash    VARCHAR(64),  -- to detect when re-embedding is needed
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- HNSW index for approximate nearest neighbor search
CREATE INDEX idx_prompt_embeddings_vector ON prompt_embeddings
    USING hnsw (embedding vector_cosine_ops)
    WITH (m = 16, ef_construction = 64);

CREATE INDEX idx_prompt_embeddings_project ON prompt_embeddings(project_id);
