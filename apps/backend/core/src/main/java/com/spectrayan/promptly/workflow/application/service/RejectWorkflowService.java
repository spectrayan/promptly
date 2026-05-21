package com.spectrayan.promptly.workflow.application.service;

import com.spectrayan.promptly.shared.domain.event.WorkflowRejected;
import com.spectrayan.promptly.shared.exception.ResourceNotFoundException;
import com.spectrayan.promptly.workflow.application.port.in.RejectWorkflowUseCase;
import com.spectrayan.promptly.workflow.application.port.out.WorkflowPersistencePort;
import com.spectrayan.promptly.workflow.application.port.out.WorkflowStepPersistencePort;
import com.spectrayan.promptly.workflow.domain.model.Workflow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class RejectWorkflowService implements RejectWorkflowUseCase {

    private final WorkflowPersistencePort workflowRepository;
    private final WorkflowStepPersistencePort stepRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Mono<Workflow> rejectWorkflow(RejectWorkflowCommand command) {
        log.info("Rejecting workflow: id={}", command.workflowId());
        return workflowRepository.findById(command.workflowId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Workflow", command.workflowId())))
                .flatMap(workflow ->
                    // Load steps from dedicated collection
                    stepRepository.findByWorkflowId(workflow.getId())
                            .collectList()
                            .flatMap(steps -> {
                                workflow.setSteps(steps);
                                workflow.rejectCurrentStep(command.reason());

                                // Persist updated steps back
                                return stepRepository.saveAll(workflow.getId(), workflow.getSteps())
                                        .collectList()
                                        .then(workflowRepository.save(workflow));
                            })
                )
                .doOnSuccess(saved -> {
                    log.info("Workflow rejected: id={}", saved.getId());
                    eventPublisher.publishEvent(new WorkflowRejected(
                            saved.getId(), saved.getPromptId(),
                            saved.getPromptId(),
                            saved.getProjectId(),
                            command.rejectedBy(), command.reason(),
                            saved.getRequestedBy()
                    ));
                });
    }
}
