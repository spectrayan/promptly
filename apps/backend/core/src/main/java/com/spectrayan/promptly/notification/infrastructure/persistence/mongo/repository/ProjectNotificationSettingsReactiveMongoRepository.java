package com.spectrayan.promptly.notification.infrastructure.persistence.mongo.repository;

import com.spectrayan.promptly.notification.infrastructure.persistence.mongo.entity.ProjectNotificationSettingsDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ProjectNotificationSettingsReactiveMongoRepository
        extends ReactiveMongoRepository<ProjectNotificationSettingsDocument, String> {

    Mono<ProjectNotificationSettingsDocument> findByProjectId(String projectId);
}
