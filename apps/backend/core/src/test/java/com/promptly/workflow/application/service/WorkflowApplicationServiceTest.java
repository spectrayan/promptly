package com.promptly.workflow.application.service;

import com.promptly.shared.exception.ResourceNotFoundException;
import com.promptly.workflow.ReviewSubmitted;
import com.promptly.workflow.WorkflowApproved;
import com.promptly.workflow.WorkflowRejected;
import com.promptly.workflow.application.port.in.ApproveWorkflowUseCase.ApproveWorkflowCommand;
import com.promptly.workflow.application.port.in.RejectWorkflowUseCase.RejectWorkflowCommand;
import com.promptly.workflow.application.port.in.SubmitReviewUseCase.SubmitReviewCommand;
import com.promptly.workflow.application.port.out.WorkflowPersistencePort;
import com.promptly.workflow.domain.model.Workflow;
import com.promptly.workflow.domain.model.WorkflowStatus;
import com.promptly.workflow.domain.model.WorkflowStep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link WorkflowApplicationService}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("WorkflowApplicationService")
class WorkflowApplicationServiceTest {

    @Mock private WorkflowPersistencePort persistencePort;
    @Mock private ApplicationEventPublisher eventPublisher;

    private WorkflowApplicationService service;

    @BeforeEach
    void setUp() {
        service = new WorkflowApplicationService(persistencePort, eventPublisher);
    }

    private static Workflow pendingWorkflowWithSteps() {
        return Workflow.builder()
                .id("wf-1").projectId("proj-1").promptId("p-1").promptVersion(1)
                .type("approval").status(WorkflowStatus.PENDING).currentStep(0)
                .requestedBy("alice@promptly.ai")
                .steps(new ArrayList<>(List.of(
                        WorkflowStep.builder().step(1).role("reviewer").assignedTo("auto").action("PENDING").build(),
                        WorkflowStep.builder().step(2).role("approver").assignedTo("auto").action("PENDING").build()
                )))
                .build();
    }

    // ═══════════════════════════════════════════════════════════════
    // submitForReview
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("submitForReview")
    class SubmitTests {

        @BeforeEach
        void stub() {
            when(persistencePort.save(any(Workflow.class)))
                    .thenAnswer(inv -> {
                        Workflow wf = inv.getArgument(0);
                        wf.setId("wf-generated");
                        return Mono.just(wf);
                    });
        }

        @Test
        @DisplayName("should create PENDING workflow with 2 steps")
        void shouldCreateWorkflow() {
            var command = new SubmitReviewCommand("p-1", "proj-1", 1, "alice");

            StepVerifier.create(service.submitForReview(command))
                    .assertNext(wf -> {
                        assertThat(wf.getStatus()).isEqualTo(WorkflowStatus.PENDING);
                        assertThat(wf.getSteps()).hasSize(2);
                        assertThat(wf.getPromptId()).isEqualTo("p-1");
                        assertThat(wf.getRequestedBy()).isEqualTo("alice");
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("should publish ReviewSubmitted event")
        void shouldPublishEvent() {
            service.submitForReview(
                    new SubmitReviewCommand("p-1", "proj-1", 2, "alice")
            ).block();

            ArgumentCaptor<ReviewSubmitted> captor = ArgumentCaptor.forClass(ReviewSubmitted.class);
            verify(eventPublisher).publishEvent(captor.capture());

            ReviewSubmitted event = captor.getValue();
            assertThat(event.promptId()).isEqualTo("p-1");
            assertThat(event.promptVersion()).isEqualTo(2);
            assertThat(event.requestedBy()).isEqualTo("alice");
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // approveWorkflow
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("approveWorkflow")
    class ApproveTests {

        @Test
        @DisplayName("should approve and transition to APPROVED status")
        void shouldApprove() {
            Workflow wf = pendingWorkflowWithSteps();
            when(persistencePort.findById("wf-1")).thenReturn(Mono.just(wf));
            when(persistencePort.save(any(Workflow.class)))
                    .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

            StepVerifier.create(service.approveWorkflow(
                    new ApproveWorkflowCommand("wf-1", "bob", "LGTM")
            ))
                    .assertNext(result -> {
                        assertThat(result.getStatus()).isEqualTo(WorkflowStatus.APPROVED);
                        assertThat(result.getSteps())
                                .allSatisfy(s -> assertThat(s.getAction()).isEqualTo("APPROVED"));
                    })
                    .verifyComplete();
        }

        @Test
        @DisplayName("should publish WorkflowApproved event when fully approved")
        void shouldPublishApprovedEvent() {
            Workflow wf = pendingWorkflowWithSteps();
            when(persistencePort.findById("wf-1")).thenReturn(Mono.just(wf));
            when(persistencePort.save(any(Workflow.class)))
                    .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

            service.approveWorkflow(
                    new ApproveWorkflowCommand("wf-1", "bob", "Ship it")
            ).block();

            ArgumentCaptor<WorkflowApproved> captor = ArgumentCaptor.forClass(WorkflowApproved.class);
            verify(eventPublisher).publishEvent(captor.capture());
            assertThat(captor.getValue().approvedBy()).isEqualTo("bob");
        }

        @Test
        @DisplayName("should fail when workflow not found")
        void workflowNotFound() {
            when(persistencePort.findById("wf-999")).thenReturn(Mono.empty());

            StepVerifier.create(service.approveWorkflow(
                    new ApproveWorkflowCommand("wf-999", "bob", "x")
            ))
                    .expectError(ResourceNotFoundException.class)
                    .verify();
        }

        @Test
        @DisplayName("should fail when workflow is already terminal")
        void terminalCannotBeApproved() {
            Workflow wf = pendingWorkflowWithSteps();
            wf.setStatus(WorkflowStatus.APPROVED);
            wf.getSteps().forEach(s -> s.approve("done"));

            when(persistencePort.findById("wf-1")).thenReturn(Mono.just(wf));

            StepVerifier.create(service.approveWorkflow(
                    new ApproveWorkflowCommand("wf-1", "bob", "again")
            ))
                    .expectError(IllegalStateException.class)
                    .verify();
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // rejectWorkflow
    // ═══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("rejectWorkflow")
    class RejectTests {

        @Test
        @DisplayName("should reject and transition to REJECTED status")
        void shouldReject() {
            Workflow wf = pendingWorkflowWithSteps();
            when(persistencePort.findById("wf-1")).thenReturn(Mono.just(wf));
            when(persistencePort.save(any(Workflow.class)))
                    .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

            StepVerifier.create(service.rejectWorkflow(
                    new RejectWorkflowCommand("wf-1", "carol", "Needs context")
            ))
                    .assertNext(result ->
                            assertThat(result.getStatus()).isEqualTo(WorkflowStatus.REJECTED))
                    .verifyComplete();
        }

        @Test
        @DisplayName("should publish WorkflowRejected event")
        void shouldPublishRejectedEvent() {
            Workflow wf = pendingWorkflowWithSteps();
            when(persistencePort.findById("wf-1")).thenReturn(Mono.just(wf));
            when(persistencePort.save(any(Workflow.class)))
                    .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

            service.rejectWorkflow(
                    new RejectWorkflowCommand("wf-1", "carol", "Policy violation")
            ).block();

            ArgumentCaptor<WorkflowRejected> captor = ArgumentCaptor.forClass(WorkflowRejected.class);
            verify(eventPublisher).publishEvent(captor.capture());

            WorkflowRejected event = captor.getValue();
            assertThat(event.rejectedBy()).isEqualTo("carol");
            assertThat(event.reason()).isEqualTo("Policy violation");
        }

        @Test
        @DisplayName("should fail when workflow not found")
        void workflowNotFound() {
            when(persistencePort.findById("wf-999")).thenReturn(Mono.empty());

            StepVerifier.create(service.rejectWorkflow(
                    new RejectWorkflowCommand("wf-999", "carol", "x")
            ))
                    .expectError(ResourceNotFoundException.class)
                    .verify();
        }
    }
}
