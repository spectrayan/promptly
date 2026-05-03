package com.promptly.notification.infrastructure.persistence.repository;

import com.promptly.notification.infrastructure.persistence.entity.ProjectNotificationSettingsDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ProjectNotificationSettingsReactiveMongoRepository
        extends ReactiveMongoRepository<ProjectNotificationSettingsDocument, String> {

    Mono<ProjectNotificationSettingsDocument> findByProjectId(String projectId);
}
