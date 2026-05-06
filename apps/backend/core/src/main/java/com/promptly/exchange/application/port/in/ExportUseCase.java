package com.promptly.exchange.application.port.in;

import com.promptly.exchange.domain.model.ExportManifest;
import reactor.core.publisher.Mono;

import java.time.Instant;

public interface ExportUseCase {

    Mono<ExportManifest> exportPrompt(String promptId, String exportedBy);

    Mono<ExportManifest> exportByProject(String projectId, Instant approvedAfter, Instant approvedBefore, String exportedBy);
}
