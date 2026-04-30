package com.promptly.workflow.application.service;

import com.promptly.shared.exception.ResourceNotFoundException;
import com.promptly.workflow.application.port.in.GetWorkflowUseCase;
import com.promptly.workflow.application.port.out.WorkflowPersistencePort;
import com.promptly.workflow.domain.model.Workflow;
import com.promptly.workflow.domain.model.WorkflowStatus;
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

    @Override
    public Mono<Workflow> getWorkflowById(String id) {
        return workflowRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Workflow", id)));
    }

    @Override
    public Flux<Workflow> getPendingWorkflows() {
        return workflowRepository.findByStatus(WorkflowStatus.PENDING)
                .concatWith(workflowRepository.findByStatus(WorkflowStatus.IN_REVIEW));
    }

    @Override
    public Flux<Workflow> getWorkflowsByPromptId(String promptId) {
        return workflowRepository.findByPromptId(promptId);
    }
}
