package com.spectrayan.promptly.exchange.application.port.in;

import com.spectrayan.promptly.exchange.domain.model.ExportManifest;
import com.spectrayan.promptly.exchange.application.service.ExportApplicationService.ImportResult;
import reactor.core.publisher.Mono;

public interface ImportUseCase {

    Mono<ImportResult> importManifest(ExportManifest manifest);
}
