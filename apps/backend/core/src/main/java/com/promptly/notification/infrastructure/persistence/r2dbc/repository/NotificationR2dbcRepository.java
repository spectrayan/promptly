package com.promptly.notification.infrastructure.persistence.r2dbc.repository;

import com.promptly.notification.infrastructure.persistence.r2dbc.entity.NotificationR2dbcEntity;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Spring Data R2DBC repository for the {@code notifications} table.
 */
public interface NotificationR2dbcRepository extends R2dbcRepository<NotificationR2dbcEntity, String> {

    @Query("""
            SELECT * FROM notifications
            WHERE user_id = :userId AND project_id = :projectId
              AND (:unreadOnly = false OR is_read = false)
              AND (:before IS NULL OR created_at < :before)
            ORDER BY created_at DESC
            LIMIT :limit
            """)
    Flux<NotificationR2dbcEntity> findByUserAndProject(String userId, String projectId,
                                                        boolean unreadOnly, Instant before, int limit);

    @Query("SELECT COUNT(*) FROM notifications WHERE user_id = :userId AND project_id = :projectId AND is_read = false")
    Mono<Long> countUnread(String userId, String projectId);

    @Modifying
    @Query("UPDATE notifications SET is_read = true WHERE id = :id AND user_id = :userId")
    Mono<Void> markAsRead(String id, String userId);

    @Modifying
    @Query("UPDATE notifications SET is_read = true WHERE user_id = :userId AND project_id = :projectId")
    Mono<Void> markAllRead(String userId, String projectId);

    @Modifying
    @Query("DELETE FROM notifications WHERE id = :id AND user_id = :userId")
    Mono<Void> deleteByIdAndUserId(String id, String userId);

    @Modifying
    @Query("DELETE FROM notifications WHERE user_id = :userId AND project_id = :projectId")
    Mono<Void> deleteAllByUserAndProject(String userId, String projectId);
}
