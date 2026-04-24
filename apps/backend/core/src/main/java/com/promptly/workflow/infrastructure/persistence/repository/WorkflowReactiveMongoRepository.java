package com.promptly.workflow.infrastructure.persistence.repository;

import com.promptly.workflow.infrastructure.persistence.entity.WorkflowDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data reactive repository for WorkflowDocument.
 */
public interface WorkflowReactiveMongoRepository extends ReactiveMongoRepository<WorkflowDocument, String> {

    Flux<WorkflowDocument> findByStatus(String status);

    Flux<WorkflowDocument> findByPromptId(String promptId);

}
