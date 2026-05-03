package com.promptly.project.infrastructure.persistence.r2dbc.repository;

import com.promptly.project.infrastructure.persistence.r2dbc.entity.ProjectR2dbcEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the {@code projects} table.
 */
public interface ProjectR2dbcRepository extends R2dbcRepository<ProjectR2dbcEntity, String> {

    Mono<Boolean> existsByName(String name);

    @Query("SELECT p.* FROM projects p INNER JOIN project_members pm ON p.id = pm.project_id WHERE pm.user_id = :userId")
    Flux<ProjectR2dbcEntity> findByMemberUserId(String userId);
}
