package com.promptly.improver.application.service;

import com.promptly.improver.application.port.in.ImprovePromptUseCase;
import com.promptly.improver.application.port.out.LlmImproverPort;
import com.promptly.prompt.PromptModuleApi;
import com.promptly.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Application service orchestrating AI-assisted prompt improvement.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImproverApplicationService implements ImprovePromptUseCase {

    private final PromptModuleApi promptModuleApi;
    private final LlmImproverPort llmImproverPort;

    @Override
    public Mono<ImprovementResult> improvePrompt(String promptId) {
        log.info("Generating improvement for prompt: {}", promptId);

        return promptModuleApi.findById(promptId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Prompt", promptId)))
                .flatMap(prompt -> {
                    String currentContent = (prompt.latestContent() != null)
                            ? prompt.latestContent()
                            : "";

                    return Mono.fromCallable(() -> llmImproverPort.improveContent(currentContent))
                            .subscribeOn(Schedulers.boundedElastic())
                            .map(result -> new ImprovementResult(
                                    promptId,
                                    currentContent,
                                    result.improvedContent(),
                                    result.summary()
                            ));
                });
    }

    @Override
    public Mono<Void> applyImprovement(String promptId, String improvedContent, String author) {
        log.info("Applying improvement to prompt: {}", promptId);
        return promptModuleApi.updatePrompt(promptId, improvedContent, "Applied AI-suggested improvement", author)
                .then();
    }

}
