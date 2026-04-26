package com.promptly.notification.application.port.in;

import com.promptly.notification.domain.model.Notification;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Inbound port for querying and mutating notifications (read, dismiss, count).
 */
public interface QueryNotificationUseCase {

    /**
     * Cursor-based fetch of notifications for a user within a project.
     *
     * @param userId     target user
     * @param projectId  project scope
     * @param unreadOnly if true, only unread notifications
     * @param before     cursor — only return items created before this timestamp (null = latest)
     * @param limit      max items to return
     */
    Flux<Notification> getByUserAndProject(String userId, String projectId,
                                            boolean unreadOnly, Instant before, int limit);

    /**
     * Count unread notifications for the bell badge.
     */
    Mono<Long> countUnread(String userId, String projectId);

    Mono<Void> markAsRead(String notificationId, String userId);

    Mono<Void> markAllRead(String userId, String projectId);

    Mono<Void> dismiss(String notificationId, String userId);
}
