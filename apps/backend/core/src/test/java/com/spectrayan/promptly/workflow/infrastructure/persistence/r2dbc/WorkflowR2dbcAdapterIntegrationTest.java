package com.spectrayan.promptly.workflow.infrastructure.persistence.r2dbc;

import com.spectrayan.promptly.AbstractR2dbcIntegrationTest;
import com.spectrayan.promptly.workflow.application.port.out.WorkflowPersistencePort;
import com.spectrayan.promptly.workflow.application.port.out.WorkflowStepPersistencePort;
import com.spectrayan.promptly.workflow.domain.model.Workflow;
import com.spectrayan.promptly.workflow.domain.model.WorkflowStatus;
import com.spectrayan.promptly.workflow.domain.model.WorkflowStep;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for Workflow + WorkflowStep R2DBC adapters.
 * Tests the two-port pattern where workflows and steps are stored separately.
 */
@SpringBootTest
class WorkflowR2dbcAdapterIntegrationTest extends AbstractR2dbcIntegrationTest {

    @Autowired
    private WorkflowPersistencePort workflowPort;

    @Autowired
    private WorkflowStepPersistencePort stepPort;



    private Workflow buildWorkflow(String projectId, String promptId) {
        return Workflow.builder()
                .id(UUID.randomUUID().toString())
                .projectId(projectId)
                .promptId(promptId)
                .promptVersion(1)
                .type("approval")
                .status(WorkflowStatus.PENDING)
                .currentStep(0)
                .requestedBy("test-user")
                .version(0L)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    private List<WorkflowStep> buildSteps() {
        return List.of(
                WorkflowStep.builder()
                        .step(1)
                        .role("reviewer")
                        .assignedTo("reviewer@test.com")
                        .action("PENDING")
                        .build(),
                WorkflowStep.builder()
                        .step(2)
                        .role("approver")
                        .assignedTo("approver@test.com")
                        .action("PENDING")
                        .build()
        );
    }

    @Test
    void shouldSaveAndRetrieveWorkflow() {
        var workflow = buildWorkflow("wf-proj", "prompt-1");

        StepVerifier.create(
                workflowPort.save(workflow)
                        .flatMap(saved -> workflowPort.findById(saved.getId()))
        )
        .assertNext(found -> {
            assertThat(found.getProjectId()).isEqualTo("wf-proj");
            assertThat(found.getPromptId()).isEqualTo("prompt-1");
            assertThat(found.getStatus()).isEqualTo(WorkflowStatus.PENDING);
            assertThat(found.getType()).isEqualTo("approval");
        })
        .verifyComplete();
    }

    @Test
    void shouldSaveAndRetrieveStepsSeparately() {
        var workflow = buildWorkflow("step-proj", "prompt-2");

        StepVerifier.create(
                workflowPort.save(workflow)
                        .flatMapMany(saved -> stepPort.saveAll(saved.getId(), buildSteps())
                                .thenMany(stepPort.findByWorkflowId(saved.getId())))
                        .collectList()
        )
        .assertNext(steps -> {
            assertThat(steps).hasSize(2);
            assertThat(steps.get(0).getStep()).isEqualTo(1);
            assertThat(steps.get(0).getRole()).isEqualTo("reviewer");
            assertThat(steps.get(1).getStep()).isEqualTo(2);
            assertThat(steps.get(1).getRole()).isEqualTo("approver");
        })
        .verifyComplete();
    }

    @Test
    void shouldFindWorkflowsByStatus() {
        var wf1 = buildWorkflow("status-proj", "p1");
        var wf2 = buildWorkflow("status-proj", "p2");
        wf2.setStatus(WorkflowStatus.APPROVED);

        StepVerifier.create(
                workflowPort.save(wf1)
                        .then(workflowPort.save(wf2))
                        .thenMany(workflowPort.findByStatus(WorkflowStatus.PENDING))
                        .collectList()
        )
        .assertNext(workflows -> {
            assertThat(workflows).allMatch(w -> w.getStatus() == WorkflowStatus.PENDING);
        })
        .verifyComplete();
    }

    @Test
    void shouldFindWorkflowsByPromptId() {
        var workflow = buildWorkflow("prompt-proj", "specific-prompt");

        StepVerifier.create(
                workflowPort.save(workflow)
                        .thenMany(workflowPort.findByPromptId("specific-prompt"))
                        .collectList()
        )
        .assertNext(workflows -> {
            assertThat(workflows).hasSizeGreaterThanOrEqualTo(1);
            assertThat(workflows.get(0).getPromptId()).isEqualTo("specific-prompt");
        })
        .verifyComplete();
    }

    @Test
    void shouldFindSpecificStepByNumber() {
        var workflow = buildWorkflow("step-find-proj", "p3");

        StepVerifier.create(
                workflowPort.save(workflow)
                        .flatMap(saved -> stepPort.saveAll(saved.getId(), buildSteps())
                                .then(stepPort.findByWorkflowIdAndStep(saved.getId(), 2)))
        )
        .assertNext(step -> {
            assertThat(step.getStep()).isEqualTo(2);
            assertThat(step.getRole()).isEqualTo("approver");
        })
        .verifyComplete();
    }

    @Test
    void shouldCascadeDeleteSteps() {
        var workflow = buildWorkflow("cascade-proj", "p4");

        StepVerifier.create(
                workflowPort.save(workflow)
                        .flatMap(saved -> stepPort.saveAll(saved.getId(), buildSteps())
                                .then(stepPort.deleteByWorkflowId(saved.getId()))
                                .thenMany(stepPort.findByWorkflowId(saved.getId()))
                                .collectList())
        )
        .assertNext(steps -> assertThat(steps).isEmpty())
        .verifyComplete();
    }
}
