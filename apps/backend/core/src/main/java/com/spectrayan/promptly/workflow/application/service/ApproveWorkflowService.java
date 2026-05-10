package com.spectrayan.promptly.workflow.application.service;

import com.spectrayan.promptly.shared.domain.event.WorkflowApproved;
import com.spectrayan.promptly.shared.exception.ResourceNotFoundException;
import com.spectrayan.promptly.workflow.application.port.in.ApproveWorkflowUseCase;
import com.spectrayan.promptly.workflow.application.port.out.WorkflowPersistencePort;
import com.spectrayan.promptly.workflow.application.port.out.WorkflowStepPersistencePort;
import com.spectrayan.promptly.workflow.domain.model.Workflow;
import com.spectrayan.promptly.workflow.domain.model.WorkflowStatus;
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
    private final WorkflowStepPersistencePort stepRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Mono<Workflow> approveWorkflow(ApproveWorkflowCommand command) {
        log.info("Approving workflow: id={}", command.workflowId());
        return workflowRepository.findById(command.workflowId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Workflow", command.workflowId())))
                .flatMap(workflow ->
                    // Load steps from dedicated collection
                    stepRepository.findByWorkflowId(workflow.getId())
                            .collectList()
                            .flatMap(steps -> {
                                workflow.setSteps(steps);
                                workflow.approveCurrentStep(command.comment());

                                // Persist updated steps back
                                return stepRepository.saveAll(workflow.getId(), workflow.getSteps())
                                        .collectList()
                                        .then(workflowRepository.save(workflow));
                            })
                )
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
    }
}
