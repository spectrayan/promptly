package com.spectrayan.promptly.scanner.infrastructure.persistence.r2dbc.repository;

import com.spectrayan.promptly.scanner.infrastructure.persistence.r2dbc.entity.ScanResultR2dbcEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the {@code scan_results} table.
 */
public interface ScanResultR2dbcRepository extends R2dbcRepository<ScanResultR2dbcEntity, String> {

    Flux<ScanResultR2dbcEntity> findByProjectId(String projectId);

    Flux<ScanResultR2dbcEntity> findByPromptId(String promptId);

    @Query("SELECT * FROM scan_results WHERE prompt_id = :promptId ORDER BY created_at DESC LIMIT 1")
    Mono<ScanResultR2dbcEntity> findLatestByPromptId(String promptId);
}
