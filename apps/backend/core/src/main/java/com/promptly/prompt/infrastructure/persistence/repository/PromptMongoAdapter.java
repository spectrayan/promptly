package com.promptly.prompt.infrastructure.persistence.repository;

import com.promptly.prompt.application.port.out.PromptPersistencePort;
import com.promptly.prompt.domain.model.Prompt;
import com.promptly.prompt.infrastructure.persistence.mapper.PromptPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Adapter implementing the domain's PromptPersistencePort port.
 * Converts between domain models and MongoDB documents using MapStruct.
 */
@Component
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
    public Flux<Prompt> findAll() {
        return mongoRepository.findAll()
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
