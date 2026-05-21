package com.spectrayan.promptly.delivery.application.service;

import com.spectrayan.promptly.delivery.application.port.in.DeliverPromptUseCase;
import com.spectrayan.promptly.prompt.PromptModuleApi;
import com.spectrayan.promptly.prompt.PromptProjection;
import com.spectrayan.promptly.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Application service for runtime prompt delivery.
 * Fetches prompts by metadata filters and returns the latest approved content.
 * <p>
 * Matching strategy: prompts are filtered by project (appId) and then
 * scored against the requested usecase/agent criteria. The best match wins.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryApplicationService implements DeliverPromptUseCase {

    private final PromptModuleApi promptModuleApi;

    @Override
    public Mono<DeliveryResponse> deliverPrompt(String appId, String usecase, String agent) {
        log.debug("Delivering prompt: appId={}, usecase={}, agent={}", appId, usecase, agent);

        return promptModuleApi.findByProjectId(appId)
                .filter(prompt -> matchesCriteria(prompt, usecase, agent))
                .collectList()
                .flatMap(matches -> {
                    if (matches.isEmpty()) {
                        return Mono.error(new ResourceNotFoundException(
                                "Prompt", "appId=" + appId + ", usecase=" + usecase));
                    }
                    // Score and pick the best match
                    PromptProjection best = matches.stream()
                            .max((a, b) -> Integer.compare(
                                    score(a, usecase, agent),
                                    score(b, usecase, agent)))
                            .orElse(matches.getFirst());

                    log.debug("Matched prompt '{}' for delivery (appId={}, usecase={}, agent={})",
                            best.name(), appId, usecase, agent);

                    return Mono.just(toDeliveryResponse(best));
                });
    }

    // ── Matching ─────────────────────────────────────────────────────

    /**
     * Returns true if the prompt matches the given usecase/agent criteria.
     * A null criterion is treated as a wildcard (always matches).
     */
    private boolean matchesCriteria(PromptProjection prompt, String usecase, String agent) {
        String name = prompt.name() != null ? prompt.name().toLowerCase() : "";
        String desc = prompt.description() != null ? prompt.description().toLowerCase() : "";
        String combined = name + " " + desc;

        boolean matchUsecase = usecase == null || combined.contains(usecase.toLowerCase());
        boolean matchAgent = agent == null || combined.contains(agent.toLowerCase());
        return matchUsecase || matchAgent;
    }

    /**
     * Scores a prompt match — higher is better.
     * Both criteria matching scores higher than just one.
     */
    private int score(PromptProjection prompt, String usecase, String agent) {
        String name = prompt.name() != null ? prompt.name().toLowerCase() : "";
        String desc = prompt.description() != null ? prompt.description().toLowerCase() : "";
        String combined = name + " " + desc;

        int score = 0;
        if (usecase != null && combined.contains(usecase.toLowerCase())) score += 2;
        if (agent != null && combined.contains(agent.toLowerCase())) score += 1;
        // Exact name match bonus
        if (usecase != null && name.equals(usecase.toLowerCase())) score += 3;
        return score;
    }

    // ── Mapping ──────────────────────────────────────────────────────

    private DeliveryResponse toDeliveryResponse(PromptProjection prompt) {
        return new DeliveryResponse(
                prompt.id(),
                prompt.name(),
                prompt.latestContent() != null ? prompt.latestContent() : "",
                prompt.currentVersion(),
                prompt.contentFormat() != null ? prompt.contentFormat() : "TEXT"
        );
    }
}
