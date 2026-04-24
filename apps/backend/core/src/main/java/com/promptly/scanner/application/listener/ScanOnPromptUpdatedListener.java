package com.promptly.scanner.application.listener;

import com.promptly.prompt.PromptCreated;
import com.promptly.prompt.PromptUpdated;
import com.promptly.scanner.application.port.in.ScanPromptUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Listens for prompt events and triggers automatic scans.
 * Uses @Async to avoid blocking the source module's reactive chain.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScanOnPromptUpdatedListener {

    private final ScanPromptUseCase scanPromptUseCase;

    @Async
    @EventListener
    void on(PromptCreated event) {
        log.info("Auto-scanning new prompt: {}", event.aggregateId());
        scanPromptUseCase.scanPrompt(event.aggregateId())
                .doOnError(e -> log.warn("Auto-scan failed for prompt {}: {}", event.aggregateId(), e.getMessage()))
                .onErrorComplete()
                .subscribe();
    }

    @Async
    @EventListener
    void on(PromptUpdated event) {
        log.info("Auto-scanning updated prompt: {}", event.aggregateId());
        scanPromptUseCase.scanPrompt(event.aggregateId())
                .doOnError(e -> log.warn("Auto-scan failed for prompt {}: {}", event.aggregateId(), e.getMessage()))
                .onErrorComplete()
                .subscribe();
    }

}
