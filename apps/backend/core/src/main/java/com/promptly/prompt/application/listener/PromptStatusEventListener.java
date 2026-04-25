package com.promptly.prompt.application.listener;

import com.promptly.prompt.application.port.out.PromptRepository;
import com.promptly.workflow.ReviewSubmitted;
import com.promptly.workflow.WorkflowApproved;
import com.promptly.workflow.WorkflowRejected;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Listens to workflow lifecycle events and updates the prompt status accordingly.
 * <p>
 * This is the cross-module integration point: the Workflow module publishes
 * domain events, and the Prompt module reacts by transitioning its own status.
 * <ul>
 *   <li>{@link ReviewSubmitted} → Prompt status becomes IN_REVIEW</li>
 *   <li>{@link WorkflowApproved} → Prompt status becomes APPROVED</li>
 *   <li>{@link WorkflowRejected} → Prompt status resets to REJECTED</li>
 * </ul>
 * All operations are fire-and-forget — failures are logged but never
 * propagate back to the workflow service.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PromptStatusEventListener {

    private final PromptRepository promptRepository;

    @EventListener
    void on(ReviewSubmitted event) {
        log.info("ReviewSubmitted → updating prompt {} to IN_REVIEW", event.promptId());
        promptRepository.findById(event.promptId())
                .doOnNext(prompt -> {
                    prompt.submitForReview();
                    log.debug("Prompt {} status set to {}", prompt.getId(), prompt.getStatus());
                })
                .flatMap(promptRepository::save)
                .doOnError(err -> log.warn("Failed to update prompt status on ReviewSubmitted: {}",
                        err.getMessage()))
                .subscribe();
    }

    @EventListener
    void on(WorkflowApproved event) {
        log.info("WorkflowApproved → updating prompt {} to APPROVED", event.promptId());
        promptRepository.findById(event.promptId())
                .doOnNext(prompt -> {
                    prompt.markApproved();
                    log.debug("Prompt {} status set to {}", prompt.getId(), prompt.getStatus());
                })
                .flatMap(promptRepository::save)
                .doOnError(err -> log.warn("Failed to update prompt status on WorkflowApproved: {}",
                        err.getMessage()))
                .subscribe();
    }

    @EventListener
    void on(WorkflowRejected event) {
        log.info("WorkflowRejected → updating prompt {} to REJECTED", event.promptId());
        promptRepository.findById(event.promptId())
                .doOnNext(prompt -> {
                    prompt.markRejected();
                    log.debug("Prompt {} status set to {}", prompt.getId(), prompt.getStatus());
                })
                .flatMap(promptRepository::save)
                .doOnError(err -> log.warn("Failed to update prompt status on WorkflowRejected: {}",
                        err.getMessage()))
                .subscribe();
    }
}
