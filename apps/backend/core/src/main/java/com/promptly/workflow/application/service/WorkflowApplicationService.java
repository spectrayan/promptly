package com.promptly.workflow.application.service;

import com.promptly.shared.exception.ResourceNotFoundException;
import com.promptly.workflow.ReviewSubmitted;
import com.promptly.workflow.WorkflowApproved;
import com.promptly.workflow.WorkflowRejected;
import com.promptly.workflow.application.port.in.*;
import com.promptly.workflow.application.port.out.WorkflowPersistencePort;
import com.promptly.workflow.domain.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Application service orchestrating the multi-step approval workflow lifecycle.
 * <p>
 * Implements all workflow use cases: submission, approval, rejection, and queries.
 * Each state transition delegates to the {@link Workflow} aggregate's domain methods,
 * which enforce business rules via {@link WorkflowSpecifications}.
 * <p>
 * Domain events ({@link ReviewSubmitted}, {@link WorkflowApproved}, {@link WorkflowRejected})
 * are published on successful state transitions for downstream consumers (audit, notification).
 *
 * @see com.promptly.workflow.domain.model.Workflow
 * @see com.promptly.workflow.domain.model.WorkflowSpecifications
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowApplicationService implements
        SubmitReviewUseCase,
        ApproveWorkflowUseCase,
        RejectWorkflowUseCase,
        GetWorkflowUseCase {

    private final WorkflowPersistencePort workflowRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Mono<Workflow> submitForReview(SubmitReviewCommand command) {
        log.info("Submitting prompt {} v{} for review", command.promptId(), command.promptVersion());

        Workflow workflow = Workflow.builder()
                .promptId(command.promptId())
                .projectId(command.projectId())
                .promptVersion(command.promptVersion())
                .type("approval")
                .status(WorkflowStatus.PENDING)
                .currentStep(1)
                .requestedBy(command.requestedBy())
                .steps(List.of(
                        WorkflowStep.builder()
                                .step(1)
                                .role("reviewer")
                                .assignedTo("auto")
                                .action("PENDING")
                                .build(),
                        WorkflowStep.builder()
                                .step(2)
                                .role("approver")
                                .assignedTo("auto")
                                .action("PENDING")
                                .build()
                ))
                .build();

        return workflowRepository.save(workflow)
                .doOnSuccess(saved -> {
                    log.info("Workflow created: id={}", saved.getId());
                    eventPublisher.publishEvent(new ReviewSubmitted(
                            saved.getId(), saved.getPromptId(),
                            saved.getPromptVersion(), saved.getRequestedBy()
                    ));
                });
    }

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
