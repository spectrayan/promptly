package com.spectrayan.promptly.scanner.infrastructure.web;

import com.spectrayan.promptly.infrastructure.in.web.api.ScannerApi;
import com.spectrayan.promptly.infrastructure.in.web.dto.FindingResponse;
import com.spectrayan.promptly.infrastructure.in.web.dto.FindingType;
import com.spectrayan.promptly.infrastructure.in.web.dto.ScanResponse;
import com.spectrayan.promptly.infrastructure.in.web.dto.ScanStatus;
import com.spectrayan.promptly.infrastructure.in.web.dto.Severity;
import com.spectrayan.promptly.scanner.application.port.in.ScanPromptUseCase;
import com.spectrayan.promptly.scanner.application.port.out.ScanResultPersistencePort;
import com.spectrayan.promptly.scanner.domain.model.ScanResult;
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
    private final ScanResultPersistencePort scanResultRepository;

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
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }

    @Override
    public Mono<ResponseEntity<Flux<ScanResponse>>> getAllScans(String projectId, ServerWebExchange exchange) {
        Flux<ScanResult> results = (projectId != null && !projectId.isBlank())
                ? scanResultRepository.findByProjectId(projectId)
                : scanPromptUseCase.getAllScanResults();

        return results
                .map(this::toResponse)
                .collectList()
                .map(list -> ResponseEntity.ok(Flux.fromIterable(list)));
    }

    // ── Domain → DTO mapping ──────────────────────────────────────────

    private ScanResponse toResponse(ScanResult result) {
        List<FindingResponse> findings = result.getFindings() == null ? List.of() : result.getFindings().stream()
                .map(f -> {
                    var finding = new FindingResponse();
                    finding.setType(safeParseFindingType(f.getType()));
                    finding.setSeverity(f.getSeverity() != null
                            ? Severity.fromValue(f.getSeverity().name())
                            : null);
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
        response.setStatus(safeParseScanStatus(result.getStatus()));
        response.setFindings(findings);
        response.setLlmProvider(result.getLlmProvider());
        response.setLlmModel(result.getLlmModel());
        response.setScannedAt(toOffsetDateTime(result.getScannedAt()));
        return response;
    }

    private FindingType safeParseFindingType(String type) {
        if (type == null) return null;
        try {
            return FindingType.fromValue(type);
        } catch (IllegalArgumentException e) {
            return null; // Ignore invalid finding types gracefully
        }
    }

    private ScanStatus safeParseScanStatus(String status) {
        if (status == null) return null;
        try {
            return ScanStatus.fromValue(status);
        } catch (IllegalArgumentException e) {
            try {
                return ScanStatus.fromValue(status.toLowerCase());
            } catch (IllegalArgumentException ex) {
                return null;
            }
        }
    }

    private OffsetDateTime toOffsetDateTime(Instant instant) {
        return instant != null ? instant.atOffset(ZoneOffset.UTC) : null;
    }

}
