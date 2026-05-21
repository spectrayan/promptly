package com.spectrayan.promptly.auth.infrastructure.persistence.mongo.repository;

import com.spectrayan.promptly.auth.infrastructure.persistence.mongo.entity.UserDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data reactive repository for UserDocument.
 */
public interface UserReactiveMongoRepository extends ReactiveMongoRepository<UserDocument, String> {

    Mono<UserDocument> findByEmail(String email);

    Mono<Boolean> existsByEmail(String email);

    Flux<UserDocument> findByEmailContainingIgnoreCaseOrDisplayNameContainingIgnoreCase(
            String email, String displayName);
}
