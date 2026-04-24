package com.promptly.auth.domain.model;

/**
 * Organization-level role.
 * Determines platform-wide permissions (e.g., creating projects).
 * Project-specific roles (VIEWER, AUTHOR, REVIEWER, APPROVER, ADMIN) are separate.
 */
public enum OrgRole {
    ORG_ADMIN,
    ORG_USER
}
