package com.spectrayan.promptly.project.infrastructure.persistence.r2dbc.repository;

import com.spectrayan.promptly.project.infrastructure.persistence.r2dbc.entity.ProjectMemberR2dbcEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the {@code project_members} table.
 */
public interface ProjectMemberR2dbcRepository extends R2dbcRepository<ProjectMemberR2dbcEntity, String> {

    Flux<ProjectMemberR2dbcEntity> findByProjectId(String projectId);

    Flux<ProjectMemberR2dbcEntity> findByUserId(String userId);

    Mono<ProjectMemberR2dbcEntity> findByProjectIdAndUserId(String projectId, String userId);

    Mono<Boolean> existsByProjectIdAndUserId(String projectId, String userId);

    Mono<Void> deleteByProjectIdAndUserId(String projectId, String userId);
}
