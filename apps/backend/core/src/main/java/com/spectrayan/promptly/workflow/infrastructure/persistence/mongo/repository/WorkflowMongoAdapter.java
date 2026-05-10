package com.spectrayan.promptly.workflow.infrastructure.persistence.mongo.repository;

import com.spectrayan.promptly.workflow.application.port.out.WorkflowPersistencePort;
import com.spectrayan.promptly.workflow.domain.model.Workflow;
import com.spectrayan.promptly.workflow.domain.model.WorkflowStatus;
import com.spectrayan.promptly.workflow.infrastructure.persistence.mongo.mapper.WorkflowPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Adapter implementing the domain's WorkflowPersistencePort port.
 */
@Component
@ConditionalOnProperty(name = "promptly.persistence.type", havingValue = "mongo", matchIfMissing = true)
@RequiredArgsConstructor
public class WorkflowMongoAdapter implements WorkflowPersistencePort {

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
