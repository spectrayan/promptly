package com.promptly.notification.application.port.in;

import com.promptly.notification.domain.model.Notification;
import com.promptly.notification.domain.model.NotificationEventType;
import com.promptly.notification.domain.model.NotificationPreference;
import com.promptly.notification.domain.model.ProjectNotificationSettings;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

/**
 * Inbound port for notification operations.
 * Combines query, mutation, and preference management.
 */
public interface NotificationUseCase {

    // ── Fan-out creation (called by event listener) ──

    /**
     * Create notifications for all eligible project members.
     * Checks project settings and user preferences before persisting.
     */
    Mono<Void> createForProject(String projectId, NotificationEventType eventType,
                                 String message, Map<String, Object> payload);

    // ── Query ──

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

    // ── Mutations ──

    Mono<Void> markAsRead(String notificationId, String userId);

    Mono<Void> markAllRead(String userId, String projectId);

    Mono<Void> dismiss(String notificationId, String userId);

    // ── Preferences ──

    Mono<NotificationPreference> getPreferences(String userId, String projectId);

    Mono<NotificationPreference> updatePreferences(String userId, String projectId,
                                                    NotificationPreference preference);

    // ── Project Settings ──

    Mono<ProjectNotificationSettings> getProjectSettings(String projectId);

    Mono<ProjectNotificationSettings> updateProjectSettings(String projectId,
                                                             ProjectNotificationSettings settings);
}
