package com.promptly.notification.application.service;

import com.promptly.notification.application.port.in.QueryNotificationUseCase;
import com.promptly.notification.application.port.out.NotificationPersistencePort;
import com.promptly.notification.domain.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Application service for querying and mutating individual notifications.
 * <p>
 * Handles: cursor-based listing, unread count, mark-as-read, and dismiss.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QueryNotificationService implements QueryNotificationUseCase {

    private final NotificationPersistencePort notifRepo;

    @Override
    public Flux<Notification> getByUserAndProject(String userId, String projectId,
                                                   boolean unreadOnly, Instant before, int limit) {
        return notifRepo.findByUserAndProject(userId, projectId, unreadOnly, before, limit);
    }

    @Override
    public Mono<Long> countUnread(String userId, String projectId) {
        return notifRepo.countByUserIdAndProjectIdAndReadFalse(userId, projectId);
    }

    @Override
    public Mono<Void> markAsRead(String notificationId, String userId) {
        return notifRepo.markAsRead(notificationId, userId);
    }

    @Override
    public Mono<Void> markAllRead(String userId, String projectId) {
        return notifRepo.markAllReadByUserAndProject(userId, projectId);
    }

    @Override
    public Mono<Void> dismiss(String notificationId, String userId) {
        return notifRepo.deleteByIdAndUserId(notificationId, userId);
    }

    @Override
    public Mono<Void> clearAll(String userId, String projectId) {
        return notifRepo.deleteAllByUserAndProject(userId, projectId);
    }
}
