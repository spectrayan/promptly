package com.promptly.project.domain.model;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.List;

/**
 * Domain model representing an organizational project — the primary security
 * boundary and resource grouping in Promptly's multi-tenant RBAC model.
 * <p>
 * All prompts, workflows, and scans are scoped to a project. User access is
 * determined by their {@link com.promptly.project.domain.model.ProjectRole} within
 * a specific project's membership.
 * <p>
 * A special well-known project named {@code __system__} is used to store
 * platform-level system prompts (see ADR-005).
 * <p>
 * Pure POJO — no framework annotations. The corresponding MongoDB document
 * is {@code ProjectDocument} in the infrastructure layer.
 *
 * @see ProjectMember
 * @see ProjectRole
 */
@Data
@Builder
public class Project {
    private String id;
    private String name;
    private String description;
    private List<String> tags;
    private String createdBy;
    private Instant createdAt;
    private Instant updatedAt;
}
