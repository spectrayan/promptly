package com.promptly.audit.infrastructure.persistence.repository;

import com.promptly.audit.application.port.out.AuditRepository;
import com.promptly.audit.domain.model.AuditEntry;
import com.promptly.audit.infrastructure.persistence.mapper.AuditPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Adapter implementing the domain's AuditRepository port.
 * Write-only — no update or delete operations.
 */
@Component
@RequiredArgsConstructor
public class AuditMongoAdapter implements AuditRepository {

    private final AuditReactiveMongoRepository mongoRepository;
    private final AuditPersistenceMapper mapper;

    @Override
    public Mono<AuditEntry> save(AuditEntry entry) {
        return mongoRepository.save(mapper.toDocument(entry))
                .map(mapper::toDomain);
    }

    @Override
    public Flux<AuditEntry> findByResourceId(String resourceId) {
        return mongoRepository.findByResourceIdOrderByTimestampDesc(resourceId)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<AuditEntry> findByActorUserId(String userId) {
        return mongoRepository.findByActorUserIdOrderByTimestampDesc(userId)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<AuditEntry> findAll() {
        return mongoRepository.findAll()
                .map(mapper::toDomain);
    }

    @Override
    public Flux<AuditEntry> findByProjectId(String projectId) {
        return mongoRepository.findByProjectIdOrderByTimestampDesc(projectId)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<AuditEntry> findByAction(String action) {
        return mongoRepository.findByActionOrderByTimestampDesc(action)
                .map(mapper::toDomain);
    }

}
