package com.promptly.delivery.application.service;

import com.promptly.delivery.application.port.in.DeliverPromptUseCase;
import com.promptly.prompt.PromptModuleApi;
import com.promptly.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Application service for runtime prompt delivery.
 * Fetches prompts by metadata filters and returns the latest approved content.
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
                .filter(prompt -> {
                    String name = prompt.getName().toLowerCase();
                    boolean matchUsecase = usecase == null || name.contains(usecase.toLowerCase());
                    boolean matchAgent = agent == null || name.contains(agent.toLowerCase());
                    return matchUsecase || matchAgent;
                })
                .next()
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(
                        "Prompt", "appId=" + appId + ", usecase=" + usecase)))
                .map(prompt -> {
                    String content = prompt.getVersions().isEmpty()
                            ? ""
                            : prompt.getVersions().get(prompt.getVersions().size() - 1).getContent();

                    return new DeliveryResponse(
                            prompt.getId(),
                            prompt.getName(),
                            content,
                            prompt.getCurrentVersion(),
                            prompt.getActiveEnvironment(),
                            prompt.getContentFormat() != null ? prompt.getContentFormat().name() : "TEXT"
                    );
                });
    }

}
