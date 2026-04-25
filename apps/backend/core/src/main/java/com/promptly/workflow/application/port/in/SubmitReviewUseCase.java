package com.promptly.workflow.application.port.in;

import com.promptly.workflow.domain.model.Workflow;
import reactor.core.publisher.Mono;

/**
 * Submit a prompt version for review/approval.
 */
public interface SubmitReviewUseCase {

    Mono<Workflow> submitForReview(SubmitReviewCommand command);

    record SubmitReviewCommand(
            String promptId,
            String projectId,
            int promptVersion,
            String requestedBy
    ) {}

}
