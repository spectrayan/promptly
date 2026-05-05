package com.promptly.notification.infrastructure.persistence.r2dbc.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.promptly.notification.application.port.out.NotificationPreferencePersistencePort;
import com.promptly.notification.domain.model.NotificationPreference;
import com.promptly.notification.infrastructure.persistence.r2dbc.entity.NotificationPreferenceR2dbcEntity;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

/**
 * R2DBC adapter implementing {@link NotificationPreferencePersistencePort} for SQL databases
 * (PostgreSQL, H2, SQLite).
 */
@Component
@ConditionalOnProperty(name = "promptly.persistence.type", havingValue = "sql")
@RequiredArgsConstructor
public class NotificationPreferenceR2dbcAdapter implements NotificationPreferencePersistencePort {

    private final NotificationPreferenceR2dbcRepository repository;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<NotificationPreference> findByUserIdAndProjectId(String userId, String projectId) {
        return repository.findByUserIdAndProjectId(userId, projectId).map(this::toDomain);
    }

    @Override
    public Mono<NotificationPreference> save(NotificationPreference preference) {
        return repository.save(toEntity(preference)).map(this::toDomain);
    }

    // ── Mapping ─────────────────────────────────────────────────────

    @SneakyThrows
    private NotificationPreferenceR2dbcEntity toEntity(NotificationPreference p) {
        return NotificationPreferenceR2dbcEntity.builder()
                .userId(p.getUserId())
                .projectId(p.getProjectId())
                .mutedEvents(p.getMutedEvents() != null ? objectMapper.writeValueAsString(p.getMutedEvents()) : "[]")
                .inAppEnabled(p.isInAppEnabled())
                .emailEnabled(p.isEmailEnabled())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    @SneakyThrows
    private NotificationPreference toDomain(NotificationPreferenceR2dbcEntity entity) {
        Set<String> mutedEvents = entity.getMutedEvents() != null
                ? objectMapper.readValue(entity.getMutedEvents(), new TypeReference<>() {})
                : new HashSet<>();

        return NotificationPreference.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .projectId(entity.getProjectId())
                .mutedEvents(mutedEvents)
                .inAppEnabled(entity.isInAppEnabled())
                .emailEnabled(entity.isEmailEnabled())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
