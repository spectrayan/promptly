package com.promptly.project.application.port.out;

import com.promptly.project.domain.model.Project;
import com.promptly.project.domain.model.ProjectMember;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProjectPersistencePort {
    Mono<Project> save(Project project);
    Mono<Project> findById(String id);
    Flux<Project> findByMemberUserId(String userId);
    Mono<Boolean> existsByName(String name);
}
