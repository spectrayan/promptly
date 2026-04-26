package com.promptly.notification.application.port.out;

import com.promptly.notification.domain.model.NotificationPreference;
import reactor.core.publisher.Mono;

/**
 * Outbound port for notification preference persistence.
 */
public interface NotificationPreferencePersistencePort {

    Mono<NotificationPreference> findByUserIdAndProjectId(String userId, String projectId);

    Mono<NotificationPreference> save(NotificationPreference preference);
}
