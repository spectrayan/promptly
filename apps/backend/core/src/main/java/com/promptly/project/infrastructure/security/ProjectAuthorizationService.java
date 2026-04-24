package com.promptly.project.infrastructure.security;

import com.promptly.project.application.port.out.ProjectMemberRepository;
import com.promptly.project.domain.model.ProjectRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Set;

/**
 * Evaluates whether a user has sufficient project-level privileges.
 * Used by controllers to enforce RBAC before executing domain logic.
 */
@Component
@RequiredArgsConstructor
public class ProjectAuthorizationService {

    private final ProjectMemberRepository memberRepository;

    /** Minimum roles needed per action */
    private static final Set<ProjectRole> WRITE_ROLES = Set.of(
            ProjectRole.AUTHOR, ProjectRole.REVIEWER, ProjectRole.APPROVER, ProjectRole.ADMIN);
    private static final Set<ProjectRole> REVIEW_ROLES = Set.of(
            ProjectRole.REVIEWER, ProjectRole.APPROVER, ProjectRole.ADMIN);
    private static final Set<ProjectRole> APPROVE_ROLES = Set.of(
            ProjectRole.APPROVER, ProjectRole.ADMIN);
    private static final Set<ProjectRole> ADMIN_ROLES = Set.of(ProjectRole.ADMIN);

    /**
     * Check if user has at least VIEWER access to the project.
     */
    public Mono<Boolean> canView(String projectId, String userId) {
        return memberRepository.existsByProjectIdAndUserId(projectId, userId);
    }

    /**
     * Check if user can create/edit prompts in the project.
     */
    public Mono<Boolean> canWrite(String projectId, String userId) {
        return hasRole(projectId, userId, WRITE_ROLES);
    }

    /**
     * Check if user can review prompts for promotion.
     */
    public Mono<Boolean> canReview(String projectId, String userId) {
        return hasRole(projectId, userId, REVIEW_ROLES);
    }

    /**
     * Check if user can approve/reject workflow steps.
     */
    public Mono<Boolean> canApprove(String projectId, String userId) {
        return hasRole(projectId, userId, APPROVE_ROLES);
    }

    /**
     * Check if user is project admin (can manage members).
     */
    public Mono<Boolean> isAdmin(String projectId, String userId) {
        return hasRole(projectId, userId, ADMIN_ROLES);
    }

    private Mono<Boolean> hasRole(String projectId, String userId, Set<ProjectRole> allowed) {
        return memberRepository.findByProjectIdAndUserId(projectId, userId)
                .map(member -> allowed.contains(member.getRole()))
                .defaultIfEmpty(false);
    }
}
