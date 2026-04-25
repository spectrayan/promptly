package com.promptly.notification.infrastructure.persistence.repository;

import com.promptly.notification.infrastructure.persistence.entity.NotificationPreferenceDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface NotificationPreferenceReactiveMongoRepository
        extends ReactiveMongoRepository<NotificationPreferenceDocument, String> {

    Mono<NotificationPreferenceDocument> findByUserIdAndProjectId(String userId, String projectId);
}
