package com.promptly.improver.application.service;

import com.promptly.improver.application.port.in.ImprovePromptUseCase;
import com.promptly.improver.application.port.out.LlmImproverPort;
import com.promptly.prompt.PromptModuleApi;
import com.promptly.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Application service orchestrating AI-assisted prompt improvement.
 * <p>
 * All LLM interactions are fully non-blocking via the reactive
 * {@link LlmImproverPort} — no {@code Schedulers.boundedElastic()}
 * thread-pool hopping needed.
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

                    return llmImproverPort.improveContent(currentContent)
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

    @Override
    public Mono<GenerateFromIdeaResult> generateFromIdea(String idea) {
        log.info("Generating prompt from idea: {}", idea.substring(0, Math.min(idea.length(), 100)));
        return llmImproverPort.generateContent(idea)
                .map(result -> new GenerateFromIdeaResult(
                        result.generatedContent(),
                        result.title(),
                        result.summary()
                ));
    }

}
