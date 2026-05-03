package com.promptly.project.infrastructure.persistence.mongo.repository;

import com.promptly.project.infrastructure.persistence.mongo.entity.ProjectMemberDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProjectMemberReactiveMongoRepository extends ReactiveMongoRepository<ProjectMemberDocument, String> {
    Flux<ProjectMemberDocument> findByProjectId(String projectId);
    Flux<ProjectMemberDocument> findByUserId(String userId);
    Mono<ProjectMemberDocument> findByProjectIdAndUserId(String projectId, String userId);
    Mono<Boolean> existsByProjectIdAndUserId(String projectId, String userId);
    Mono<Void> deleteByProjectIdAndUserId(String projectId, String userId);
}
