package com.promptly.project.infrastructure.persistence.r2dbc.repository;

import com.promptly.project.application.port.out.ProjectMemberPersistencePort;
import com.promptly.project.domain.model.ProjectMember;
import com.promptly.project.domain.model.ProjectRole;
import com.promptly.project.infrastructure.persistence.r2dbc.entity.ProjectMemberR2dbcEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * R2DBC adapter implementing {@link ProjectMemberPersistencePort} for PostgreSQL.
 */
@Component
@ConditionalOnProperty(name = "promptly.persistence.type", havingValue = "postgres")
@RequiredArgsConstructor
public class ProjectMemberR2dbcAdapter implements ProjectMemberPersistencePort {

    private final ProjectMemberR2dbcRepository repository;

    @Override
    public Mono<ProjectMember> save(ProjectMember member) {
        return repository.save(toEntity(member)).map(this::toDomain);
    }

    @Override
    public Flux<ProjectMember> findByProjectId(String projectId) {
        return repository.findByProjectId(projectId).map(this::toDomain);
    }

    @Override
    public Flux<ProjectMember> findByUserId(String userId) {
        return repository.findByUserId(userId).map(this::toDomain);
    }

    @Override
    public Mono<ProjectMember> findByProjectIdAndUserId(String projectId, String userId) {
        return repository.findByProjectIdAndUserId(projectId, userId).map(this::toDomain);
    }

    @Override
    public Mono<Boolean> existsByProjectIdAndUserId(String projectId, String userId) {
        return repository.existsByProjectIdAndUserId(projectId, userId);
    }

    @Override
    public Mono<Void> deleteByProjectIdAndUserId(String projectId, String userId) {
        return repository.deleteByProjectIdAndUserId(projectId, userId);
    }

    // ── Mapping ─────────────────────────────────────────────────────

    private ProjectMemberR2dbcEntity toEntity(ProjectMember member) {
        return ProjectMemberR2dbcEntity.builder()
                .projectId(member.getProjectId())
                .userId(member.getUserId())
                .role(member.getRole() != null ? member.getRole().name() : ProjectRole.VIEWER.name())
                .addedBy(member.getAddedBy())
                .addedAt(member.getAddedAt())
                .build();
    }

    private ProjectMember toDomain(ProjectMemberR2dbcEntity entity) {
        return ProjectMember.builder()
                .id(entity.getId())
                .projectId(entity.getProjectId())
                .userId(entity.getUserId())
                .role(entity.getRole() != null ? ProjectRole.valueOf(entity.getRole()) : ProjectRole.VIEWER)
                .addedBy(entity.getAddedBy())
                .addedAt(entity.getAddedAt())
                .build();
    }
}
