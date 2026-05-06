package com.promptly.scanner.infrastructure.persistence.r2dbc.repository;

import com.promptly.scanner.application.port.out.ScanResultPersistencePort;
import com.promptly.scanner.domain.model.ScanResult;
import com.promptly.scanner.infrastructure.persistence.r2dbc.converter.FindingList;
import com.promptly.scanner.infrastructure.persistence.r2dbc.entity.ScanResultR2dbcEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * R2DBC adapter implementing {@link ScanResultPersistencePort} for SQL databases.
 * <p>
 * No {@code ObjectMapper} needed — the {@link FindingList} field is handled
 * by R2DBC converters automatically.
 */
@Component
@ConditionalOnProperty(name = "promptly.persistence.type", havingValue = "sql")
@RequiredArgsConstructor
public class ScanResultR2dbcAdapter implements ScanResultPersistencePort {

    private final ScanResultR2dbcRepository repository;

    @Override
    public Mono<ScanResult> save(ScanResult scanResult) {
        return repository.save(toEntity(scanResult)).map(this::toDomain);
    }

    @Override
    public Mono<ScanResult> findLatestByPromptId(String promptId) {
        return repository.findLatestByPromptId(promptId).map(this::toDomain);
    }

    @Override
    public Flux<ScanResult> findAll() {
        return repository.findAll().map(this::toDomain);
    }

    @Override
    public Flux<ScanResult> findByProjectId(String projectId) {
        return repository.findByProjectId(projectId).map(this::toDomain);
    }

    @Override
    public Flux<ScanResult> findByPromptId(String promptId) {
        return repository.findByPromptId(promptId).map(this::toDomain);
    }

    // ── Mapping ─────────────────────────────────────────────────────

    private ScanResultR2dbcEntity toEntity(ScanResult scanResult) {
        return ScanResultR2dbcEntity.builder()
                .id(scanResult.getId())
                .projectId(scanResult.getProjectId())
                .promptId(scanResult.getPromptId())
                .promptVersion(scanResult.getPromptVersion())
                .overallScore(scanResult.getOverallScore())
                .status(scanResult.getStatus())
                .llmProvider(scanResult.getLlmProvider())
                .llmModel(scanResult.getLlmModel())
                .scannedBy(scanResult.getScannedBy())
                .findings(new FindingList(scanResult.getFindings()))
                .scannedAt(scanResult.getScannedAt())
                .version(scanResult.getVersion() != null && scanResult.getVersion() > 0 ? scanResult.getVersion() : null)
                .createdAt(scanResult.getCreatedAt())
                .updatedAt(scanResult.getUpdatedAt())
                .build();
    }

    private ScanResult toDomain(ScanResultR2dbcEntity entity) {
        return ScanResult.builder()
                .id(entity.getId())
                .projectId(entity.getProjectId())
                .promptId(entity.getPromptId())
                .promptVersion(entity.getPromptVersion())
                .overallScore(entity.getOverallScore())
                .status(entity.getStatus())
                .llmProvider(entity.getLlmProvider())
                .llmModel(entity.getLlmModel())
                .scannedBy(entity.getScannedBy())
                .findings(entity.getFindings() != null ? entity.getFindings().toList() : null)
                .scannedAt(entity.getScannedAt())
                .version(entity.getVersion())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
