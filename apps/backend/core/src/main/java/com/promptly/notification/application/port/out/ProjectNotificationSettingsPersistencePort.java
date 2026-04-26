package com.promptly.notification.application.port.out;

import com.promptly.notification.domain.model.ProjectNotificationSettings;
import reactor.core.publisher.Mono;

/**
 * Outbound port for project-level notification settings persistence.
 */
public interface ProjectNotificationSettingsPersistencePort {

    Mono<ProjectNotificationSettings> findByProjectId(String projectId);

    Mono<ProjectNotificationSettings> save(ProjectNotificationSettings settings);
}
