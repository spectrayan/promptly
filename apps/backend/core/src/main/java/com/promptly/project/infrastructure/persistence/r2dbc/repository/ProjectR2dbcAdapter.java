package com.promptly.project.infrastructure.persistence.r2dbc.repository;

import com.promptly.project.application.port.out.ProjectPersistencePort;
import com.promptly.project.domain.model.Project;
import com.promptly.project.infrastructure.persistence.r2dbc.entity.ProjectR2dbcEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * R2DBC adapter implementing {@link ProjectPersistencePort} for SQL databases.
 */
@Component
@ConditionalOnProperty(name = "promptly.persistence.type", havingValue = "sql")
@RequiredArgsConstructor
public class ProjectR2dbcAdapter implements ProjectPersistencePort {

    private final ProjectR2dbcRepository repository;

    @Override
    public Mono<Project> save(Project project) {
        return repository.save(toEntity(project)).map(this::toDomain);
    }

    @Override
    public Mono<Project> findById(String id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Flux<Project> findByMemberUserId(String userId) {
        return repository.findByMemberUserId(userId).map(this::toDomain);
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return repository.existsByName(name);
    }

    // ── Mapping ─────────────────────────────────────────────────────

    private ProjectR2dbcEntity toEntity(Project project) {
        return ProjectR2dbcEntity.builder()
                .name(project.getName())
                .description(project.getDescription())
                .tags(project.getTags() != null ? project.getTags() : List.of())
                .createdBy(project.getCreatedBy())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }

    private Project toDomain(ProjectR2dbcEntity entity) {
        return Project.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .tags(entity.getTags() != null ? entity.getTags() : List.of())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
