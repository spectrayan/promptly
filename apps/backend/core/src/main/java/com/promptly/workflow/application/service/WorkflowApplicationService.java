package com.promptly.workflow.application.service;

import com.promptly.shared.exception.ResourceNotFoundException;
import com.promptly.workflow.ReviewSubmitted;
import com.promptly.workflow.WorkflowApproved;
import com.promptly.workflow.WorkflowRejected;
import com.promptly.workflow.application.port.in.*;
import com.promptly.workflow.application.port.out.WorkflowRepository;
import com.promptly.workflow.domain.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Application service orchestrating workflow use cases.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowApplicationService implements
        SubmitReviewUseCase,
        ApproveWorkflowUseCase,
        RejectWorkflowUseCase,
        GetWorkflowUseCase {

    private final WorkflowRepository workflowRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Mono<Workflow> submitForReview(SubmitReviewCommand command) {
        log.info("Submitting prompt {} v{} for review", command.promptId(), command.promptVersion());

        Workflow workflow = Workflow.builder()
                .promptId(command.promptId())
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
                                .action("pending")
                                .build(),
                        WorkflowStep.builder()
                                .step(2)
                                .role("approver")
                                .assignedTo("auto")
                                .action("pending")
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
    public Mono<Workflow> approveWorkflow(String workflowId, String approvedBy, String comment) {
        log.info("Approving workflow: id={}", workflowId);
        return workflowRepository.findById(workflowId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Workflow", workflowId)))
                .flatMap(workflow -> {
                    workflow.approveCurrentStep(comment);
                    return workflowRepository.save(workflow)
                            .doOnSuccess(saved -> {
                                if (saved.getStatus() == WorkflowStatus.APPROVED) {
                                    log.info("Workflow fully approved: id={}", saved.getId());
                                    eventPublisher.publishEvent(new WorkflowApproved(
                                            saved.getId(), saved.getPromptId(),
                                            saved.getPromptId(), // TODO: enrich with prompt name via lookup
                                            null, // TODO: enrich with projectId via prompt lookup
                                            approvedBy,
                                            null  // TODO: enrich with requester email via user lookup
                                    ));
                                }
                            });
                });
    }

    @Override
    public Mono<Workflow> rejectWorkflow(String workflowId, String rejectedBy, String reason) {
        log.info("Rejecting workflow: id={}", workflowId);
        return workflowRepository.findById(workflowId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Workflow", workflowId)))
                .flatMap(workflow -> {
                    workflow.rejectCurrentStep(reason);
                    return workflowRepository.save(workflow)
                            .doOnSuccess(saved -> {
                                log.info("Workflow rejected: id={}", saved.getId());
                                eventPublisher.publishEvent(new WorkflowRejected(
                                        saved.getId(), saved.getPromptId(),
                                        saved.getPromptId(), // TODO: enrich with prompt name
                                        null, // TODO: enrich with projectId
                                        rejectedBy, reason,
                                        null  // TODO: enrich with requester email
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
