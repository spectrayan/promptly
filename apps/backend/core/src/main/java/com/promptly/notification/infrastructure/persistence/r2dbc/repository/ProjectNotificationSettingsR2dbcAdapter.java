package com.promptly.notification.infrastructure.persistence.r2dbc.repository;

import com.promptly.notification.application.port.out.ProjectNotificationSettingsPersistencePort;
import com.promptly.notification.domain.model.NotificationEventType;
import com.promptly.notification.domain.model.ProjectNotificationSettings;
import com.promptly.notification.infrastructure.persistence.r2dbc.entity.ProjectNotificationSettingsR2dbcEntity;
import com.promptly.shared.config.r2dbc.converter.JsonColumn;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Set;

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
                .enabledEvents(JsonColumn.ofOrEmptyArray(s.getEnabledEvents()))
                .updatedAt(s.getUpdatedAt())
                .build();
    }

    private ProjectNotificationSettings toDomain(ProjectNotificationSettingsR2dbcEntity entity) {
        Set<String> enabledEvents = entity.getEnabledEvents() != null
                ? entity.getEnabledEvents().toSet()
                : NotificationEventType.allKeys();

        return ProjectNotificationSettings.builder()
                .id(entity.getId())
                .projectId(entity.getProjectId())
                .enabledEvents(enabledEvents)
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
