package com.spectrayan.promptly.notification.infrastructure.persistence.r2dbc.repository;

import com.spectrayan.promptly.notification.application.port.out.NotificationPreferencePersistencePort;
import com.spectrayan.promptly.notification.domain.model.NotificationPreference;
import com.spectrayan.promptly.notification.infrastructure.persistence.r2dbc.entity.NotificationPreferenceR2dbcEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

/**
 * R2DBC adapter implementing {@link NotificationPreferencePersistencePort} for SQL databases.
 */
@Component
@ConditionalOnProperty(name = "promptly.persistence.type", havingValue = "sql")
@RequiredArgsConstructor
public class NotificationPreferenceR2dbcAdapter implements NotificationPreferencePersistencePort {

    private final NotificationPreferenceR2dbcRepository repository;

    @Override
    public Mono<NotificationPreference> findByUserIdAndProjectId(String userId, String projectId) {
        return repository.findByUserIdAndProjectId(userId, projectId).map(this::toDomain);
    }

    @Override
    public Mono<NotificationPreference> save(NotificationPreference preference) {
        return repository.save(toEntity(preference)).map(this::toDomain);
    }

    // ── Mapping ─────────────────────────────────────────────────────

    private NotificationPreferenceR2dbcEntity toEntity(NotificationPreference p) {
        return NotificationPreferenceR2dbcEntity.builder()
                .userId(p.getUserId())
                .projectId(p.getProjectId())
                .mutedEvents(p.getMutedEvents() != null ? p.getMutedEvents() : Set.of())
                .inAppEnabled(p.isInAppEnabled())
                .emailEnabled(p.isEmailEnabled())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    private NotificationPreference toDomain(NotificationPreferenceR2dbcEntity entity) {
        return NotificationPreference.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .projectId(entity.getProjectId())
                .mutedEvents(entity.getMutedEvents() != null ? entity.getMutedEvents() : new HashSet<>())
                .inAppEnabled(entity.isInAppEnabled())
                .emailEnabled(entity.isEmailEnabled())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
