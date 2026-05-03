package com.promptly.scanner.infrastructure.persistence.mongo.repository;

import com.promptly.scanner.application.port.out.ScanResultPersistencePort;
import com.promptly.scanner.domain.model.ScanResult;
import com.promptly.scanner.infrastructure.persistence.mongo.mapper.ScanResultPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Adapter implementing the domain's ScanResultPersistencePort port.
 */
@Component
@ConditionalOnProperty(name = "promptly.persistence.type", havingValue = "mongo", matchIfMissing = true)
@RequiredArgsConstructor
public class ScanResultMongoAdapter implements ScanResultPersistencePort {

    private final ScanResultReactiveMongoRepository mongoRepository;
    private final ScanResultPersistenceMapper mapper;

    @Override
    public Mono<ScanResult> save(ScanResult scanResult) {
        return mongoRepository.save(mapper.toDocument(scanResult))
                .map(mapper::toDomain);
    }

    @Override
    public Mono<ScanResult> findLatestByPromptId(String promptId) {
        return mongoRepository.findByPromptIdOrderByPromptVersionDesc(promptId)
                .next()
                .map(mapper::toDomain);
    }

    @Override
    public Flux<ScanResult> findAll() {
        return mongoRepository.findAll()
                .map(mapper::toDomain);
    }

    @Override
    public Flux<ScanResult> findByProjectId(String projectId) {
        return mongoRepository.findByProjectIdOrderByScannedAtDesc(projectId)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<ScanResult> findByPromptId(String promptId) {
        return mongoRepository.findByPromptIdOrderByPromptVersionDesc(promptId)
                .map(mapper::toDomain);
    }

}
