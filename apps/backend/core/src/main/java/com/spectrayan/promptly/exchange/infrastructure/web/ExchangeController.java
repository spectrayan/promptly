package com.spectrayan.promptly.exchange.infrastructure.web;

import com.spectrayan.promptly.exchange.application.port.in.ExportUseCase;
import com.spectrayan.promptly.exchange.application.port.in.ImportUseCase;
import com.spectrayan.promptly.infrastructure.in.web.api.ExchangeApi;
import com.spectrayan.promptly.infrastructure.in.web.dto.ExportManifest;
import com.spectrayan.promptly.infrastructure.in.web.dto.ImportResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@RestController
@RequiredArgsConstructor
public class ExchangeController implements ExchangeApi {

    private final ExportUseCase exportUseCase;
    private final ImportUseCase importUseCase;
    private final ExchangeWebMapper mapper;

    @Override
    public Mono<ResponseEntity<ExportManifest>> exportByProject(
            String projectId,
            OffsetDateTime approvedAfter,
            OffsetDateTime approvedBefore,
            String xExportedBy,
            ServerWebExchange exchange) {

        return exportUseCase.exportByProject(
                        projectId,
                        approvedAfter != null ? approvedAfter.toInstant() : null,
                        approvedBefore != null ? approvedBefore.toInstant() : null,
                        xExportedBy)
                .map(mapper::toDto)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<ExportManifest>> exportPrompt(
            String promptId,
            String xExportedBy,
            ServerWebExchange exchange) {

        return exportUseCase.exportPrompt(promptId, xExportedBy)
                .map(mapper::toDto)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<ImportResultResponse>> importManifest(
            Mono<ExportManifest> exportManifestMono,
            ServerWebExchange exchange) {

        return exportManifestMono
                .map(mapper::toDomain)
                .flatMap(importUseCase::importManifest)
                .map(result -> {
                    ImportResultResponse response = new ImportResultResponse();
                    response.setStatus("completed");
                    response.setImported(result.imported());
                    response.setFailed(result.failed());
                    response.setImportedPromptIds(result.importedPromptIds());
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(IllegalArgumentException.class, e -> {
                    ImportResultResponse err = new ImportResultResponse();
                    err.setStatus("failed");
                    err.setError(e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(err));
                });
    }
}
