package com.promptly.notification.infrastructure.persistence.r2dbc.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.promptly.notification.application.port.out.ProjectNotificationSettingsPersistencePort;
import com.promptly.notification.domain.model.NotificationEventType;
import com.promptly.notification.domain.model.ProjectNotificationSettings;
import com.promptly.notification.infrastructure.persistence.r2dbc.entity.ProjectNotificationSettingsR2dbcEntity;
import io.r2dbc.postgresql.codec.Json;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Set;

/**
 * R2DBC adapter implementing {@link ProjectNotificationSettingsPersistencePort} for PostgreSQL.
 */
@Component
@ConditionalOnProperty(name = "promptly.persistence.type", havingValue = "postgres")
@RequiredArgsConstructor
public class ProjectNotificationSettingsR2dbcAdapter implements ProjectNotificationSettingsPersistencePort {

    private final ProjectNotificationSettingsR2dbcRepository repository;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<ProjectNotificationSettings> findByProjectId(String projectId) {
        return repository.findByProjectId(projectId).map(this::toDomain);
    }

    @Override
    public Mono<ProjectNotificationSettings> save(ProjectNotificationSettings settings) {
        return repository.save(toEntity(settings)).map(this::toDomain);
    }

    // ── Mapping ─────────────────────────────────────────────────────

    @SneakyThrows
    private ProjectNotificationSettingsR2dbcEntity toEntity(ProjectNotificationSettings s) {
        return ProjectNotificationSettingsR2dbcEntity.builder()
                .projectId(s.getProjectId())
                .enabledEvents(s.getEnabledEvents() != null ? Json.of(objectMapper.writeValueAsString(s.getEnabledEvents())) : Json.of("[]"))
                .updatedAt(s.getUpdatedAt())
                .build();
    }

    @SneakyThrows
    private ProjectNotificationSettings toDomain(ProjectNotificationSettingsR2dbcEntity entity) {
        Set<String> enabledEvents = entity.getEnabledEvents() != null
                ? objectMapper.readValue(entity.getEnabledEvents().asString(), new TypeReference<>() {})
                : NotificationEventType.allKeys();

        return ProjectNotificationSettings.builder()
                .id(entity.getId())
                .projectId(entity.getProjectId())
                .enabledEvents(enabledEvents)
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
