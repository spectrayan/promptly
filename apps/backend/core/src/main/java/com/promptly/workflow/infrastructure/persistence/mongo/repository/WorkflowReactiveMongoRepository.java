package com.promptly.workflow.infrastructure.persistence.mongo.repository;

import com.promptly.workflow.infrastructure.persistence.mongo.entity.WorkflowDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data reactive repository for WorkflowDocument.
 */
public interface WorkflowReactiveMongoRepository extends ReactiveMongoRepository<WorkflowDocument, String> {

    Flux<WorkflowDocument> findByStatus(String status);

    Flux<WorkflowDocument> findByPromptId(String promptId);

    Flux<WorkflowDocument> findByProjectIdOrderByCreatedAtDesc(String projectId);

}
