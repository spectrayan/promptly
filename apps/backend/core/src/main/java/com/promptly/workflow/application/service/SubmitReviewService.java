package com.promptly.workflow.application.service;

import com.promptly.shared.domain.event.ReviewSubmitted;
import com.promptly.workflow.application.port.in.SubmitReviewUseCase;
import com.promptly.workflow.application.port.out.WorkflowPersistencePort;
import com.promptly.workflow.application.port.out.WorkflowStepPersistencePort;
import com.promptly.workflow.domain.model.Workflow;
import com.promptly.workflow.domain.model.WorkflowStatus;
import com.promptly.workflow.domain.model.WorkflowStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmitReviewService implements SubmitReviewUseCase {

    private final WorkflowPersistencePort workflowRepository;
    private final WorkflowStepPersistencePort stepRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Mono<Workflow> submitForReview(SubmitReviewCommand command) {
        log.info("Submitting prompt {} v{} for review", command.promptId(), command.promptVersion());

        List<WorkflowStep> steps = List.of(
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
        );

        Workflow workflow = Workflow.builder()
                .promptId(command.promptId())
                .projectId(command.projectId())
                .promptVersion(command.promptVersion())
                .type("approval")
                .status(WorkflowStatus.PENDING)
                .currentStep(1)
                .requestedBy(command.requestedBy())
                .steps(steps)
                .build();

        return workflowRepository.save(workflow)
                .flatMap(saved -> stepRepository.saveAll(saved.getId(), steps)
                        .collectList()
                        .doOnSuccess(savedSteps -> saved.setSteps(savedSteps))
                        .thenReturn(saved))
                .doOnSuccess(saved -> {
                    log.info("Workflow created: id={}", saved.getId());
                    eventPublisher.publishEvent(new ReviewSubmitted(
                            saved.getId(), saved.getPromptId(),
                            saved.getPromptVersion(), saved.getRequestedBy()
                    ));
                });
    }
}
