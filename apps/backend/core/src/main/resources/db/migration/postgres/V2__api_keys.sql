-- ════════════════════════════════════════════════════════════════════
-- V2__api_keys.sql
-- API Key Authentication for Runtime Delivery — PostgreSQL
-- ════════════════════════════════════════════════════════════════════

CREATE TABLE api_keys (
    id              VARCHAR(36)  PRIMARY KEY,
    project_id      VARCHAR(36)  NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    name            VARCHAR(200) NOT NULL,
    key_prefix      VARCHAR(20)  NOT NULL,
    key_hash        VARCHAR(64)  NOT NULL UNIQUE,
    roles           VARCHAR(500) DEFAULT 'ROLE_API_KEY',
    revoked         BOOLEAN      NOT NULL DEFAULT FALSE,
    revoked_at      TIMESTAMP WITH TIME ZONE,
    expires_at      TIMESTAMP WITH TIME ZONE,
    last_used_at    TIMESTAMP WITH TIME ZONE,
    created_by      VARCHAR(100),
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_api_keys_project ON api_keys(project_id);
CREATE INDEX idx_api_keys_hash ON api_keys(key_hash);
