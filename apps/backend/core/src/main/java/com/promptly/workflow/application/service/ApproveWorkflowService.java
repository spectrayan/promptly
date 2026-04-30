package com.promptly.workflow.application.service;

import com.promptly.shared.domain.event.WorkflowApproved;
import com.promptly.shared.exception.ResourceNotFoundException;
import com.promptly.workflow.application.port.in.ApproveWorkflowUseCase;
import com.promptly.workflow.application.port.out.WorkflowPersistencePort;
import com.promptly.workflow.domain.model.Workflow;
import com.promptly.workflow.domain.model.WorkflowStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApproveWorkflowService implements ApproveWorkflowUseCase {

    private final WorkflowPersistencePort workflowRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Mono<Workflow> approveWorkflow(ApproveWorkflowCommand command) {
        log.info("Approving workflow: id={}", command.workflowId());
        return workflowRepository.findById(command.workflowId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Workflow", command.workflowId())))
                .flatMap(workflow -> {
                    workflow.approveCurrentStep(command.comment());
                    return workflowRepository.save(workflow)
                            .doOnSuccess(saved -> {
                                if (saved.getStatus() == WorkflowStatus.APPROVED) {
                                    log.info("Workflow fully approved: id={}", saved.getId());
                                    eventPublisher.publishEvent(new WorkflowApproved(
                                            saved.getId(), saved.getPromptId(),
                                            saved.getPromptId(),
                                            saved.getProjectId(),
                                            command.approvedBy(),
                                            saved.getRequestedBy()
                                    ));
                                }
                            });
                });
    }
}
