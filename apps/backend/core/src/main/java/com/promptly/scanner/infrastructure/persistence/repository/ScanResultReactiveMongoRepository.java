package com.promptly.scanner.infrastructure.persistence.repository;

import com.promptly.scanner.infrastructure.persistence.entity.ScanResultDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data reactive repository for ScanResultDocument.
 */
public interface ScanResultReactiveMongoRepository extends ReactiveMongoRepository<ScanResultDocument, String> {

    Flux<ScanResultDocument> findByPromptIdOrderByPromptVersionDesc(String promptId);

    Flux<ScanResultDocument> findByProjectIdOrderByScannedAtDesc(String projectId);

}
