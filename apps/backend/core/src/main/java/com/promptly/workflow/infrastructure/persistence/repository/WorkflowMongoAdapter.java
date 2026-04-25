package com.promptly.workflow.infrastructure.persistence.repository;

import com.promptly.workflow.application.port.out.WorkflowRepository;
import com.promptly.workflow.domain.model.Workflow;
import com.promptly.workflow.domain.model.WorkflowStatus;
import com.promptly.workflow.infrastructure.persistence.mapper.WorkflowPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Adapter implementing the domain's WorkflowRepository port.
 */
@Component
@RequiredArgsConstructor
public class WorkflowMongoAdapter implements WorkflowRepository {

    private final WorkflowReactiveMongoRepository mongoRepository;
    private final WorkflowPersistenceMapper mapper;

    @Override
    public Mono<Workflow> save(Workflow workflow) {
        return mongoRepository.save(mapper.toDocument(workflow))
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Workflow> findById(String id) {
        return mongoRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Workflow> findByStatus(WorkflowStatus status) {
        return mongoRepository.findByStatus(status.name())
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Workflow> findByPromptId(String promptId) {
        return mongoRepository.findByPromptId(promptId)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Workflow> findByProjectId(String projectId) {
        return mongoRepository.findByProjectIdOrderByCreatedAtDesc(projectId)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Workflow> findAll() {
        return mongoRepository.findAll()
                .map(mapper::toDomain);
    }

}
