package com.promptly.audit.infrastructure.persistence.repository;

import com.promptly.audit.infrastructure.persistence.entity.AuditDocument;
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
