package com.spectrayan.promptly.scanner.application.port.in;

import com.spectrayan.promptly.scanner.domain.model.ScanResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Inbound port for triggering and retrieving vulnerability scans.
 */
public interface ScanPromptUseCase {

    Mono<ScanResult> scanPrompt(String promptId);

    Mono<ScanResult> getLatestScanResult(String promptId);

    Flux<ScanResult> getAllScanResults();

}
