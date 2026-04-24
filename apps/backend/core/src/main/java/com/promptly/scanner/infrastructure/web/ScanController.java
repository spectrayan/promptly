package com.promptly.scanner.infrastructure.web;

import com.promptly.infrastructure.in.web.api.ScannerApi;
import com.promptly.infrastructure.in.web.dto.FindingResponse;
import com.promptly.infrastructure.in.web.dto.FindingType;
import com.promptly.infrastructure.in.web.dto.ScanResponse;
import com.promptly.infrastructure.in.web.dto.ScanStatus;
import com.promptly.infrastructure.in.web.dto.Severity;
import com.promptly.scanner.application.port.in.ScanPromptUseCase;
import com.promptly.scanner.domain.model.ScanResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * REST controller for the Vulnerability Scanner module.
 * Implements the contract-first {@link ScannerApi} interface generated from the OpenAPI specification.
 */
@RestController
@RequiredArgsConstructor
public class ScanController implements ScannerApi {

    private final ScanPromptUseCase scanPromptUseCase;

    @Override
    public Mono<ResponseEntity<ScanResponse>> triggerScan(
            String promptId, ServerWebExchange exchange) {
        return scanPromptUseCase.scanPrompt(promptId)
                .map(this::toResponse)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<ScanResponse>> getLatestScanResult(
            String promptId, ServerWebExchange exchange) {
        return scanPromptUseCase.getLatestScanResult(promptId)
                .map(this::toResponse)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Flux<ScanResponse>>> getAllScans(ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(
                scanPromptUseCase.getAllScanResults().map(this::toResponse)
        ));
    }

    // ── Domain → DTO mapping ──────────────────────────────────────────

    private ScanResponse toResponse(ScanResult result) {
        List<FindingResponse> findings = result.getFindings().stream()
                .map(f -> {
                    var finding = new FindingResponse();
                    finding.setType(f.getType() != null ? FindingType.fromValue(f.getType()) : null);
                    finding.setSeverity(f.getSeverity() != null ? Severity.fromValue(f.getSeverity().name()) : null);
                    finding.setTitle(f.getTitle());
                    finding.setDescription(f.getDescription());
                    finding.setRemediation(f.getRemediation());
                    return finding;
                })
                .toList();

        var response = new ScanResponse();
        response.setId(result.getId());
        response.setPromptId(result.getPromptId());
        response.setPromptVersion(result.getPromptVersion());
        response.setOverallScore(result.getOverallScore());
        response.setStatus(result.getStatus() != null ? ScanStatus.fromValue(result.getStatus()) : null);
        response.setFindings(findings);
        response.setLlmProvider(result.getLlmProvider());
        response.setLlmModel(result.getLlmModel());
        response.setScannedAt(toOffsetDateTime(result.getScannedAt()));
        return response;
    }

    private OffsetDateTime toOffsetDateTime(Instant instant) {
        return instant != null ? instant.atOffset(ZoneOffset.UTC) : null;
    }

}
