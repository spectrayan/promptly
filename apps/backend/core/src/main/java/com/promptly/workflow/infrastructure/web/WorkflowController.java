package com.promptly.workflow.infrastructure.web;

import com.promptly.infrastructure.in.web.api.WorkflowsApi;
import com.promptly.infrastructure.in.web.dto.ApproveRejectRequest;
import com.promptly.infrastructure.in.web.dto.SubmitReviewRequest;
import com.promptly.infrastructure.in.web.dto.StepAction;
import com.promptly.infrastructure.in.web.dto.WorkflowResponse;
import com.promptly.infrastructure.in.web.dto.WorkflowStatus;
import com.promptly.infrastructure.in.web.dto.WorkflowStepResponse;
import com.promptly.workflow.application.port.in.*;
import com.promptly.workflow.application.port.out.WorkflowRepository;
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
    private final WorkflowRepository workflowRepository;

    @Override
    public Mono<ResponseEntity<WorkflowResponse>> submitForReview(
            Mono<SubmitReviewRequest> submitReviewRequest, ServerWebExchange exchange) {
        return submitReviewRequest
                .map(req -> new SubmitReviewUseCase.SubmitReviewCommand(
                        req.getPromptId(),
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

        Flux<Workflow> workflows;
        if (projectId != null && !projectId.isBlank()) {
            workflows = workflowRepository.findByProjectId(projectId);
        } else if (promptId != null) {
            workflows = getWorkflowUseCase.getWorkflowsByPromptId(promptId);
        } else if (Boolean.TRUE.equals(pendingOnly)) {
            workflows = getWorkflowUseCase.getPendingWorkflows();
        } else {
            workflows = getWorkflowUseCase.getPendingWorkflows();
        }
        return Mono.just(ResponseEntity.ok(workflows.map(this::toResponse)));
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
                .flatMap(req -> approveWorkflowUseCase.approveWorkflow(id, req.getActor(), req.getComment()))
                .map(this::toResponse)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<WorkflowResponse>> rejectWorkflow(
            String id, Mono<ApproveRejectRequest> approveRejectRequest, ServerWebExchange exchange) {
        return approveRejectRequest
                .flatMap(req -> rejectWorkflowUseCase.rejectWorkflow(id, req.getActor(), req.getComment()))
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
