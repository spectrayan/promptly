package com.promptly.project.infrastructure.persistence.mongo.repository;

import com.promptly.project.infrastructure.persistence.mongo.entity.ProjectDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ProjectReactiveMongoRepository extends ReactiveMongoRepository<ProjectDocument, String> {
    Mono<Boolean> existsByName(String name);
}
