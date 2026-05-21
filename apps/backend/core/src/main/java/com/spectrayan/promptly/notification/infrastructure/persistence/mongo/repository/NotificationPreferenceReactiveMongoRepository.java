package com.spectrayan.promptly.notification.infrastructure.persistence.mongo.repository;

import com.spectrayan.promptly.notification.infrastructure.persistence.mongo.entity.NotificationPreferenceDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface NotificationPreferenceReactiveMongoRepository
        extends ReactiveMongoRepository<NotificationPreferenceDocument, String> {

    Mono<NotificationPreferenceDocument> findByUserIdAndProjectId(String userId, String projectId);
}
