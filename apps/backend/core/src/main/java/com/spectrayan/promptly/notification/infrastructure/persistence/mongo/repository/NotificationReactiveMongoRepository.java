package com.spectrayan.promptly.notification.infrastructure.persistence.mongo.repository;

import com.spectrayan.promptly.notification.infrastructure.persistence.mongo.entity.NotificationDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

public interface NotificationReactiveMongoRepository extends ReactiveMongoRepository<NotificationDocument, String> {

    Flux<NotificationDocument> findByUserIdAndProjectIdAndCreatedAtBeforeOrderByCreatedAtDesc(
            String userId, String projectId, Instant before);

    Flux<NotificationDocument> findByUserIdAndProjectIdAndReadAndCreatedAtBeforeOrderByCreatedAtDesc(
            String userId, String projectId, boolean read, Instant before);

    Mono<Long> countByUserIdAndProjectIdAndRead(String userId, String projectId, boolean read);

    Mono<Void> deleteByIdAndUserId(String id, String userId);
}
