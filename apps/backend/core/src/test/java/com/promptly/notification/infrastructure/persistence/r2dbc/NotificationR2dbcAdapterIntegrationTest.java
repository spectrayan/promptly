package com.promptly.notification.infrastructure.persistence.r2dbc;

import com.promptly.AbstractR2dbcIntegrationTest;
import com.promptly.notification.application.port.out.NotificationPersistencePort;
import com.promptly.notification.domain.model.Notification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for Notification R2DBC adapter.
 * Verifies save, cursor-based queries, mark-as-read, and delete operations.
 */
@SpringBootTest
class NotificationR2dbcAdapterIntegrationTest extends AbstractR2dbcIntegrationTest {

    @Autowired
    private NotificationPersistencePort notifPort;

    private Notification buildNotification(String userId, String projectId, boolean read) {
        return Notification.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .projectId(projectId)
                .type("prompt.created")
                .title("Prompt Created")
                .message("A new prompt was created in your project")
                .icon("add_circle")
                .payload(Map.of("promptId", "p-1", "author", "alice"))
                .read(read)
                .createdAt(Instant.now())
                .build();
    }

    @Test
    void shouldSaveAndQueryNotificationsByUserAndProject() {
        var userId = "notif-user-" + UUID.randomUUID();
        var projectId = "notif-proj-" + UUID.randomUUID();
        var notif = buildNotification(userId, projectId, false);

        StepVerifier.create(
                notifPort.save(notif)
                        .thenMany(notifPort.findByUserAndProject(userId, projectId, false, Instant.now().plusSeconds(1), 10))
                        .collectList()
        )
        .assertNext(notifications -> {
            assertThat(notifications).hasSize(1);
            assertThat(notifications.get(0).getTitle()).isEqualTo("Prompt Created");
            assertThat(notifications.get(0).getUserId()).isEqualTo(userId);
        })
        .verifyComplete();
    }

    @Test
    void shouldFilterUnreadOnlyNotifications() {
        var userId = "unread-user-" + UUID.randomUUID();
        var projectId = "unread-proj-" + UUID.randomUUID();

        var readNotif = buildNotification(userId, projectId, true);
        var unreadNotif = buildNotification(userId, projectId, false);

        StepVerifier.create(
                notifPort.save(readNotif)
                        .then(notifPort.save(unreadNotif))
                        .thenMany(notifPort.findByUserAndProject(userId, projectId, true, Instant.now().plusSeconds(1), 10))
                        .collectList()
        )
        .assertNext(notifications -> {
            assertThat(notifications).hasSize(1);
            assertThat(notifications.get(0).isRead()).isFalse();
        })
        .verifyComplete();
    }

    @Test
    void shouldCountUnreadNotifications() {
        var userId = "count-user-" + UUID.randomUUID();
        var projectId = "count-proj-" + UUID.randomUUID();

        StepVerifier.create(
                notifPort.save(buildNotification(userId, projectId, false))
                        .then(notifPort.save(buildNotification(userId, projectId, false)))
                        .then(notifPort.save(buildNotification(userId, projectId, true)))
                        .then(notifPort.countByUserIdAndProjectIdAndReadFalse(userId, projectId))
        )
        .assertNext(count -> assertThat(count).isEqualTo(2L))
        .verifyComplete();
    }

    @Test
    void shouldMarkNotificationAsRead() {
        var userId = "mark-user-" + UUID.randomUUID();
        var projectId = "mark-proj-" + UUID.randomUUID();
        var notif = buildNotification(userId, projectId, false);

        StepVerifier.create(
                notifPort.save(notif)
                        .flatMap(saved -> notifPort.markAsRead(saved.getId(), userId)
                                .then(notifPort.countByUserIdAndProjectIdAndReadFalse(userId, projectId)))
        )
        .assertNext(count -> assertThat(count).isEqualTo(0L))
        .verifyComplete();
    }

    @Test
    void shouldMarkAllReadByUserAndProject() {
        var userId = "markall-user-" + UUID.randomUUID();
        var projectId = "markall-proj-" + UUID.randomUUID();

        StepVerifier.create(
                notifPort.save(buildNotification(userId, projectId, false))
                        .then(notifPort.save(buildNotification(userId, projectId, false)))
                        .then(notifPort.markAllReadByUserAndProject(userId, projectId))
                        .then(notifPort.countByUserIdAndProjectIdAndReadFalse(userId, projectId))
        )
        .assertNext(count -> assertThat(count).isEqualTo(0L))
        .verifyComplete();
    }

    @Test
    void shouldDeleteNotificationByIdAndUserId() {
        var userId = "del-user-" + UUID.randomUUID();
        var projectId = "del-proj-" + UUID.randomUUID();
        var notif = buildNotification(userId, projectId, false);

        StepVerifier.create(
                notifPort.save(notif)
                        .flatMap(saved -> notifPort.deleteByIdAndUserId(saved.getId(), userId)
                                .thenMany(notifPort.findByUserAndProject(userId, projectId, false, Instant.now().plusSeconds(1), 10))
                                .collectList())
        )
        .assertNext(list -> assertThat(list).isEmpty())
        .verifyComplete();
    }

    @Test
    void shouldDeleteAllByUserAndProject() {
        var userId = "delall-user-" + UUID.randomUUID();
        var projectId = "delall-proj-" + UUID.randomUUID();

        StepVerifier.create(
                notifPort.save(buildNotification(userId, projectId, false))
                        .then(notifPort.save(buildNotification(userId, projectId, true)))
                        .then(notifPort.deleteAllByUserAndProject(userId, projectId))
                        .thenMany(notifPort.findByUserAndProject(userId, projectId, false, Instant.now().plusSeconds(1), 10))
                        .collectList()
        )
        .assertNext(list -> assertThat(list).isEmpty())
        .verifyComplete();
    }
}
