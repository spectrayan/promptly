package com.spectrayan.promptly.notification.infrastructure.persistence.r2dbc.repository;

import com.spectrayan.promptly.notification.infrastructure.persistence.r2dbc.entity.NotificationPreferenceR2dbcEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the {@code notification_preferences} table.
 */
public interface NotificationPreferenceR2dbcRepository extends R2dbcRepository<NotificationPreferenceR2dbcEntity, String> {

    Mono<NotificationPreferenceR2dbcEntity> findByUserIdAndProjectId(String userId, String projectId);
}
