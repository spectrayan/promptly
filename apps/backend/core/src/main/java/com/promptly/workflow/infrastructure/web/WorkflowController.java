package com.promptly.workflow.infrastructure.web;

import com.promptly.infrastructure.in.web.api.WorkflowsApi;
import com.promptly.infrastructure.in.web.dto.ApproveRejectRequest;
import com.promptly.infrastructure.in.web.dto.SubmitReviewRequest;
import com.promptly.infrastructure.in.web.dto.StepAction;
import com.promptly.infrastructure.in.web.dto.WorkflowResponse;
import com.promptly.infrastructure.in.web.dto.WorkflowStatus;
import com.promptly.infrastructure.in.web.dto.WorkflowStepResponse;
import com.promptly.prompt.application.port.out.PromptPersistencePort;
import com.promptly.workflow.application.port.in.*;
import com.promptly.workflow.application.port.out.WorkflowPersistencePort;
import com.promptly.workflow.domain.model.Workflow;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * REST controller for the Workflow Engine module.
 * Implements the contract-first {@link WorkflowsApi} interface generated from the OpenAPI specification.
 */
@RestController
@RequiredArgsConstructor
public class WorkflowController implements WorkflowsApi {

    private final SubmitReviewUseCase submitReviewUseCase;
    private final ApproveWorkflowUseCase approveWorkflowUseCase;
    private final RejectWorkflowUseCase rejectWorkflowUseCase;
    private final GetWorkflowUseCase getWorkflowUseCase;
    private final WorkflowPersistencePort workflowRepository;
    private final PromptPersistencePort promptRepository;

    @Override
    public Mono<ResponseEntity<WorkflowResponse>> submitForReview(
            Mono<SubmitReviewRequest> submitReviewRequest, ServerWebExchange exchange) {
        return submitReviewRequest
                .map(req -> new SubmitReviewUseCase.SubmitReviewCommand(
                        req.getPromptId(),
                        req.getProjectId(),
                        req.getPromptVersion(),
                        req.getRequestedBy()
                ))
                .flatMap(submitReviewUseCase::submitForReview)
                .map(this::toResponse)
                .map(r -> ResponseEntity.status(HttpStatus.CREATED).body(r));
    }

    @Override
    public Mono<ResponseEntity<Flux<WorkflowResponse>>> listWorkflows(
            String projectId, String promptId, Boolean pendingOnly, ServerWebExchange exchange) {

        if (projectId != null && !projectId.isBlank()) {
            // Find all promptIds in this project, then return workflows
            // that have this projectId OR whose promptId belongs to this project.
            Flux<WorkflowResponse> responseFlux = promptRepository.findByProjectId(projectId)
                    .map(p -> p.getId())
                    .collect(Collectors.toSet())
                    .flatMapMany(promptIds ->
                            workflowRepository.findAll()
                                    .filter(wf -> projectId.equals(wf.getProjectId())
                                            || promptIds.contains(wf.getPromptId()))
                    )
                    .map(this::toResponse);
            return Mono.just(ResponseEntity.ok(responseFlux));
        } else if (promptId != null) {
            Flux<WorkflowResponse> flux = getWorkflowUseCase.getWorkflowsByPromptId(promptId).map(this::toResponse);
            return Mono.just(ResponseEntity.ok(flux));
        } else if (Boolean.TRUE.equals(pendingOnly)) {
            Flux<WorkflowResponse> flux = getWorkflowUseCase.getPendingWorkflows().map(this::toResponse);
            return Mono.just(ResponseEntity.ok(flux));
        } else {
            Flux<WorkflowResponse> flux = workflowRepository.findAll().map(this::toResponse);
            return Mono.just(ResponseEntity.ok(flux));
        }
    }

    @Override
    public Mono<ResponseEntity<WorkflowResponse>> getWorkflow(
            String id, ServerWebExchange exchange) {
        return getWorkflowUseCase.getWorkflowById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<WorkflowResponse>> approveWorkflow(
            String id, Mono<ApproveRejectRequest> approveRejectRequest, ServerWebExchange exchange) {
        return approveRejectRequest
                .map(req -> new ApproveWorkflowUseCase.ApproveWorkflowCommand(id, req.getActor(), req.getComment()))
                .flatMap(approveWorkflowUseCase::approveWorkflow)
                .map(this::toResponse)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<WorkflowResponse>> rejectWorkflow(
            String id, Mono<ApproveRejectRequest> approveRejectRequest, ServerWebExchange exchange) {
        return approveRejectRequest
                .map(req -> new RejectWorkflowUseCase.RejectWorkflowCommand(id, req.getActor(), req.getComment()))
                .flatMap(rejectWorkflowUseCase::rejectWorkflow)
                .map(this::toResponse)
                .map(ResponseEntity::ok);
    }

    // ── Domain → DTO mapping ──────────────────────────────────────────

    private WorkflowResponse toResponse(Workflow workflow) {
        List<WorkflowStepResponse> steps = workflow.getSteps().stream()
                .map(s -> {
                    var step = new WorkflowStepResponse();
                    step.setStep(s.getStep());
                    step.setRole(s.getRole());
                    step.setAssignedTo(s.getAssignedTo());
                    step.setAction(s.getAction() != null
                            ? StepAction.fromValue(s.getAction())
                            : null);
                    step.setComment(s.getComment());
                    step.setActedAt(toOffsetDateTime(s.getActedAt()));
                    return step;
                })
                .toList();

        var response = new WorkflowResponse();
        response.setId(workflow.getId());
        response.setPromptId(workflow.getPromptId());
        response.setPromptVersion(workflow.getPromptVersion());
        response.setType(workflow.getType());
        response.setStatus(workflow.getStatus() != null
                ? WorkflowStatus.fromValue(workflow.getStatus().name())
                : null);
        response.setCurrentStep(workflow.getCurrentStep());
        response.setRequestedBy(workflow.getRequestedBy());
        response.setSteps(steps);
        response.setCreatedAt(toOffsetDateTime(workflow.getCreatedAt()));
        response.setUpdatedAt(toOffsetDateTime(workflow.getUpdatedAt()));
        return response;
    }

    private OffsetDateTime toOffsetDateTime(Instant instant) {
        return instant != null ? instant.atOffset(ZoneOffset.UTC) : null;
    }

}
