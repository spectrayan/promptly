package com.promptly.notification.application.port.out;

import com.promptly.notification.domain.model.Notification;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Outbound port for notification persistence.
 */
public interface NotificationPersistencePort {

    Mono<Notification> save(Notification notification);

    /**
     * Cursor-based query: returns notifications for a user in a project,
     * ordered by createdAt descending, filtered by read status and cursor.
     */
    Flux<Notification> findByUserAndProject(String userId, String projectId,
                                             boolean unreadOnly, Instant before, int limit);

    Mono<Long> countByUserIdAndProjectIdAndReadFalse(String userId, String projectId);

    Mono<Void> markAsRead(String id, String userId);

    Mono<Void> markAllReadByUserAndProject(String userId, String projectId);

    Mono<Void> deleteByIdAndUserId(String id, String userId);

    Mono<Void> deleteAllByUserAndProject(String userId, String projectId);
}
