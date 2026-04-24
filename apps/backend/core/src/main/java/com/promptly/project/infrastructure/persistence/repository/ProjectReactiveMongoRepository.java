package com.promptly.project.infrastructure.persistence.repository;

import com.promptly.project.infrastructure.persistence.entity.ProjectDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ProjectReactiveMongoRepository extends ReactiveMongoRepository<ProjectDocument, String> {
    Mono<Boolean> existsByName(String name);
}
