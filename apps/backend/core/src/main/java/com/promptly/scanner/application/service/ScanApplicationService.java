package com.promptly.scanner.application.service;

import com.promptly.prompt.PromptModuleApi;
import com.promptly.scanner.ScanCompleted;
import com.promptly.scanner.application.port.in.ScanPromptUseCase;
import com.promptly.scanner.application.port.out.LlmScannerPort;
import com.promptly.scanner.application.port.out.ScanResultRepository;
import com.promptly.scanner.domain.model.ScanResult;
import com.promptly.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Application service orchestrating vulnerability scanning.
 * The LLM call runs on a bounded elastic scheduler (virtual threads).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScanApplicationService implements ScanPromptUseCase {

    private final ScanResultRepository scanResultRepository;
    private final LlmScannerPort llmScannerPort;
    private final PromptModuleApi promptModuleApi;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Mono<ScanResult> scanPrompt(String promptId) {
        log.info("Initiating scan for prompt: {}", promptId);

        return promptModuleApi.findById(promptId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Prompt", promptId)))
                .flatMap(prompt -> {
                    String latestContent = prompt.getVersions().isEmpty()
                            ? ""
                            : prompt.getVersions().get(prompt.getVersions().size() - 1).getContent();

                    return Mono.fromCallable(() ->
                                    llmScannerPort.analyzePrompt(promptId, prompt.getCurrentVersion(), latestContent)
                            )
                            .subscribeOn(Schedulers.boundedElastic())
                            .flatMap(scanResultRepository::save)
                            .doOnSuccess(saved -> {
                                log.info("Scan completed: promptId={}, score={}, status={}",
                                        promptId, saved.getOverallScore(), saved.getStatus());
                                eventPublisher.publishEvent(new ScanCompleted(
                                        saved.getId(), saved.getPromptId(),
                                        prompt.getProjectId(),
                                        saved.getPromptVersion(), saved.getStatus(),
                                        saved.getOverallScore(), saved.getFindings().size()
                                ));
                            });
                });
    }

    @Override
    public Mono<ScanResult> getLatestScanResult(String promptId) {
        return scanResultRepository.findLatestByPromptId(promptId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("ScanResult", promptId)));
    }

    @Override
    public Flux<ScanResult> getAllScanResults() {
        return scanResultRepository.findAll();
    }

}
