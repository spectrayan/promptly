package com.promptly.workflow.application.service;

import com.promptly.shared.domain.event.WorkflowRejected;
import com.promptly.shared.exception.ResourceNotFoundException;
import com.promptly.workflow.application.port.in.RejectWorkflowUseCase;
import com.promptly.workflow.application.port.out.WorkflowPersistencePort;
import com.promptly.workflow.domain.model.Workflow;
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
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Mono<Workflow> rejectWorkflow(RejectWorkflowCommand command) {
        log.info("Rejecting workflow: id={}", command.workflowId());
        return workflowRepository.findById(command.workflowId())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Workflow", command.workflowId())))
                .flatMap(workflow -> {
                    workflow.rejectCurrentStep(command.reason());
                    return workflowRepository.save(workflow)
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
                });
    }
}
