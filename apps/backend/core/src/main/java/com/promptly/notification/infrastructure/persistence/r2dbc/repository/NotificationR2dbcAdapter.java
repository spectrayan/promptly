package com.promptly.notification.infrastructure.persistence.r2dbc.repository;

import com.promptly.notification.application.port.out.NotificationPersistencePort;
import com.promptly.notification.domain.model.Notification;
import com.promptly.notification.infrastructure.persistence.r2dbc.entity.NotificationR2dbcEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * R2DBC adapter implementing {@link NotificationPersistencePort} for SQL databases.
 */
@Component
@ConditionalOnProperty(name = "promptly.persistence.type", havingValue = "sql")
@RequiredArgsConstructor
public class NotificationR2dbcAdapter implements NotificationPersistencePort {

    private final NotificationR2dbcRepository repository;

    @Override
    public Mono<Notification> save(Notification notification) {
        return repository.save(toEntity(notification)).map(this::toDomain);
    }

    @Override
    public Flux<Notification> findByUserAndProject(String userId, String projectId,
                                                    boolean unreadOnly, Instant before, int limit) {
        return repository.findByUserAndProject(userId, projectId, unreadOnly, before, limit)
                .map(this::toDomain);
    }

    @Override
    public Mono<Long> countByUserIdAndProjectIdAndReadFalse(String userId, String projectId) {
        return repository.countUnread(userId, projectId);
    }

    @Override
    public Mono<Void> markAsRead(String id, String userId) {
        return repository.markAsRead(id, userId);
    }

    @Override
    public Mono<Void> markAllReadByUserAndProject(String userId, String projectId) {
        return repository.markAllRead(userId, projectId);
    }

    @Override
    public Mono<Void> deleteByIdAndUserId(String id, String userId) {
        return repository.deleteByIdAndUserId(id, userId);
    }

    @Override
    public Mono<Void> deleteAllByUserAndProject(String userId, String projectId) {
        return repository.deleteAllByUserAndProject(userId, projectId);
    }

    // ── Mapping ─────────────────────────────────────────────────────

    private NotificationR2dbcEntity toEntity(Notification n) {
        return NotificationR2dbcEntity.builder()
                .userId(n.getUserId())
                .projectId(n.getProjectId())
                .type(n.getType())
                .title(n.getTitle())
                .message(n.getMessage())
                .icon(n.getIcon())
                .payload(n.getPayload())
                .read(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }

    private Notification toDomain(NotificationR2dbcEntity entity) {
        return Notification.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .projectId(entity.getProjectId())
                .type(entity.getType())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .icon(entity.getIcon())
                .payload(entity.getPayload())
                .read(entity.isRead())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
