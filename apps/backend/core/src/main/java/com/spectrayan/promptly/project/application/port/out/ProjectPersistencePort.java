package com.spectrayan.promptly.project.application.port.out;

import com.spectrayan.promptly.project.domain.model.Project;
import com.spectrayan.promptly.project.domain.model.ProjectMember;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Outbound port for project persistence.
 * Implemented by {@code ProjectMongoAdapter} in the infrastructure layer.
 * <p>
 * Returns domain models — never MongoDB documents.
 */
public interface ProjectPersistencePort {
    Mono<Project> save(Project project);
    Mono<Project> findById(String id);
    Flux<Project> findByMemberUserId(String userId);
    Mono<Boolean> existsByName(String name);
}
