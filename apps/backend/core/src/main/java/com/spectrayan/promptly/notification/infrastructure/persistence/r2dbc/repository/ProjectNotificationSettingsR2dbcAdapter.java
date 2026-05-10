package com.spectrayan.promptly.notification.infrastructure.persistence.r2dbc.repository;

import com.spectrayan.promptly.notification.application.port.out.ProjectNotificationSettingsPersistencePort;
import com.spectrayan.promptly.notification.domain.model.NotificationEventType;
import com.spectrayan.promptly.notification.domain.model.ProjectNotificationSettings;
import com.spectrayan.promptly.notification.infrastructure.persistence.r2dbc.entity.ProjectNotificationSettingsR2dbcEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Set;

/**
 * R2DBC adapter implementing {@link ProjectNotificationSettingsPersistencePort} for SQL databases.
 */
@Component
@ConditionalOnProperty(name = "promptly.persistence.type", havingValue = "sql")
@RequiredArgsConstructor
public class ProjectNotificationSettingsR2dbcAdapter implements ProjectNotificationSettingsPersistencePort {

    private final ProjectNotificationSettingsR2dbcRepository repository;

    @Override
    public Mono<ProjectNotificationSettings> findByProjectId(String projectId) {
        return repository.findByProjectId(projectId).map(this::toDomain);
    }

    @Override
    public Mono<ProjectNotificationSettings> save(ProjectNotificationSettings settings) {
        return repository.save(toEntity(settings)).map(this::toDomain);
    }

    private ProjectNotificationSettingsR2dbcEntity toEntity(ProjectNotificationSettings s) {
        return ProjectNotificationSettingsR2dbcEntity.builder()
                .projectId(s.getProjectId())
                .enabledEvents(s.getEnabledEvents() != null ? s.getEnabledEvents() : Set.of())
                .updatedAt(s.getUpdatedAt())
                .build();
    }

    private ProjectNotificationSettings toDomain(ProjectNotificationSettingsR2dbcEntity entity) {
        return ProjectNotificationSettings.builder()
                .id(entity.getId())
                .projectId(entity.getProjectId())
                .enabledEvents(entity.getEnabledEvents() != null ? entity.getEnabledEvents() : NotificationEventType.allKeys())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
