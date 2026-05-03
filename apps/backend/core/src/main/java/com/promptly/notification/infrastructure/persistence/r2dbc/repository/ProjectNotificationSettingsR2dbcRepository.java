package com.promptly.notification.infrastructure.persistence.r2dbc.repository;

import com.promptly.notification.infrastructure.persistence.r2dbc.entity.ProjectNotificationSettingsR2dbcEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the {@code project_notification_settings} table.
 */
public interface ProjectNotificationSettingsR2dbcRepository extends R2dbcRepository<ProjectNotificationSettingsR2dbcEntity, String> {

    Mono<ProjectNotificationSettingsR2dbcEntity> findByProjectId(String projectId);
}
