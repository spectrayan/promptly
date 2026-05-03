package com.promptly.prompt.infrastructure.persistence.mongo.repository;

import com.promptly.prompt.application.port.out.PromptPersistencePort;
import com.promptly.prompt.domain.model.Prompt;
import com.promptly.prompt.infrastructure.persistence.mongo.mapper.PromptPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Adapter implementing the domain's PromptPersistencePort port.
 * Converts between domain models and MongoDB documents using MapStruct.
 */
@Component
@ConditionalOnProperty(name = "promptly.persistence.type", havingValue = "mongo", matchIfMissing = true)
@RequiredArgsConstructor
public class PromptMongoAdapter implements PromptPersistencePort {

    private final PromptReactiveMongoRepository mongoRepository;
    private final PromptPersistenceMapper mapper;

    @Override
    public Mono<Prompt> save(Prompt prompt) {
        return mongoRepository.save(mapper.toDocument(prompt))
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Prompt> findById(String id) {
        return mongoRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Prompt> findByProjectId(String projectId) {
        return mongoRepository.findByProjectId(projectId)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Prompt> findByProjectId(String projectId, Pageable pageable) {
        return mongoRepository.findByProjectId(projectId, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Prompt> findAll() {
        return mongoRepository.findAll()
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Prompt> findAll(Pageable pageable) {
        return mongoRepository.findAllBy(pageable)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return mongoRepository.deleteById(id);
    }

    @Override
    public Mono<Boolean> existsByNameAndProjectId(String name, String projectId) {
        return mongoRepository.existsByNameAndProjectId(name, projectId);
    }

}
