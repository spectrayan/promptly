package com.promptly.scanner.application.port.out;

import com.promptly.scanner.domain.model.ScanResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Outbound port for scan result persistence.
 */
public interface ScanResultRepository {

    Mono<ScanResult> save(ScanResult scanResult);

    Mono<ScanResult> findLatestByPromptId(String promptId);

    Flux<ScanResult> findAll();

    Flux<ScanResult> findByPromptId(String promptId);

}
