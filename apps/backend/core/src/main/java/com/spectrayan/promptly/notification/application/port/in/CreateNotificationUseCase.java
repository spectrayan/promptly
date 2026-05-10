package com.spectrayan.promptly.notification.application.port.in;

import com.spectrayan.promptly.notification.domain.model.NotificationEventType;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Inbound port for fan-out notification creation.
 * Called by event listeners when domain events occur.
 */
public interface CreateNotificationUseCase {

    /**
     * Create notifications for all eligible project members.
     * Checks project settings and user preferences before persisting.
     */
    Mono<Void> createForProject(String projectId, NotificationEventType eventType,
                                 String message, Map<String, Object> payload);
}
