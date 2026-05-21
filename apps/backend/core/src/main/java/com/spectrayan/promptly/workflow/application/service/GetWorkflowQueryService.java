package com.spectrayan.promptly.workflow.application.service;

import com.spectrayan.promptly.shared.exception.ResourceNotFoundException;
import com.spectrayan.promptly.workflow.application.port.in.GetWorkflowUseCase;
import com.spectrayan.promptly.workflow.application.port.out.WorkflowPersistencePort;
import com.spectrayan.promptly.workflow.application.port.out.WorkflowStepPersistencePort;
import com.spectrayan.promptly.workflow.domain.model.Workflow;
import com.spectrayan.promptly.workflow.domain.model.WorkflowStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetWorkflowQueryService implements GetWorkflowUseCase {

    private final WorkflowPersistencePort workflowRepository;
    private final WorkflowStepPersistencePort stepRepository;

    @Override
    public Mono<Workflow> getWorkflowById(String id) {
        return workflowRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Workflow", id)))
                .flatMap(this::hydrateSteps);
    }

    @Override
    public Flux<Workflow> getPendingWorkflows() {
        return workflowRepository.findByStatus(WorkflowStatus.PENDING)
                .concatWith(workflowRepository.findByStatus(WorkflowStatus.IN_REVIEW))
                .flatMap(this::hydrateSteps);
    }

    @Override
    public Flux<Workflow> getWorkflowsByPromptId(String promptId) {
        return workflowRepository.findByPromptId(promptId)
                .flatMap(this::hydrateSteps);
    }

    @Override
    public Flux<Workflow> getWorkflowsByProjectId(String projectId) {
        return workflowRepository.findByProjectId(projectId)
                .flatMap(this::hydrateSteps);
    }

    @Override
    public Flux<Workflow> getAllWorkflows() {
        return workflowRepository.findAll()
                .flatMap(this::hydrateSteps);
    }

    /**
     * Hydrates a workflow aggregate with its steps from the dedicated collection.
     */
    private Mono<Workflow> hydrateSteps(Workflow workflow) {
        return stepRepository.findByWorkflowId(workflow.getId())
                .collectList()
                .doOnNext(workflow::setSteps)
                .thenReturn(workflow);
    }
}
