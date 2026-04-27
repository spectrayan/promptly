package com.promptly.project.application.port.out;

import com.promptly.project.domain.model.ProjectMember;
import com.promptly.project.domain.model.ProjectRole;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Outbound port for project member persistence and RBAC queries.
 * Implemented by {@code ProjectMemberMongoAdapter} in the infrastructure layer.
 * <p>
 * Supports membership CRUD and existence checks for duplicate prevention.
 */
public interface ProjectMemberPersistencePort {
    Mono<ProjectMember> save(ProjectMember member);
    Flux<ProjectMember> findByProjectId(String projectId);
    Flux<ProjectMember> findByUserId(String userId);
    Mono<ProjectMember> findByProjectIdAndUserId(String projectId, String userId);
    Mono<Boolean> existsByProjectIdAndUserId(String projectId, String userId);
    Mono<Void> deleteByProjectIdAndUserId(String projectId, String userId);
}
