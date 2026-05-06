package com.promptly.exchange.application.port.in;

import com.promptly.exchange.domain.model.ExportManifest;
import com.promptly.exchange.application.service.ExportApplicationService.ImportResult;
import reactor.core.publisher.Mono;

public interface ImportUseCase {

    Mono<ImportResult> importManifest(ExportManifest manifest);
}
