package com.spectrayan.promptly.audit.infrastructure.persistence.mongo.repository;

import com.spectrayan.promptly.audit.infrastructure.persistence.mongo.entity.AuditDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data reactive repository for AuditDocument.
 */
public interface AuditReactiveMongoRepository extends ReactiveMongoRepository<AuditDocument, String> {

    Flux<AuditDocument> findByResourceIdOrderByTimestampDesc(String resourceId);

    Flux<AuditDocument> findByActorUserIdOrderByTimestampDesc(String userId);

    Flux<AuditDocument> findByActionOrderByTimestampDesc(String action);

    Flux<AuditDocument> findByProjectIdOrderByTimestampDesc(String projectId);

}
