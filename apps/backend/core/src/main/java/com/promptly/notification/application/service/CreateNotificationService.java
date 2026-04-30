package com.promptly.notification.application.service;

import com.promptly.notification.application.port.in.CreateNotificationUseCase;
import com.promptly.notification.application.port.out.NotificationPersistencePort;
import com.promptly.notification.application.port.out.NotificationPreferencePersistencePort;
import com.promptly.notification.application.port.out.ProjectNotificationSettingsPersistencePort;
import com.promptly.notification.domain.model.Notification;
import com.promptly.notification.domain.model.NotificationEventType;
import com.promptly.notification.domain.model.NotificationPreference;
import com.promptly.notification.domain.model.ProjectNotificationSettings;
import com.promptly.project.application.port.out.ProjectMemberPersistencePort;
import com.promptly.notification.infrastructure.delivery.SseNotificationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

/**
 * Application service for fan-out notification creation.
 * <p>
 * Handles the lifecycle of creating notifications for project members:
 * - Checks project-level settings (is this event type enabled?)
 * - Looks up eligible members
 * - Checks per-user preferences (has user muted this type?)
 * - Persists notification
 * - Pushes via SSE for real-time delivery
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CreateNotificationService implements CreateNotificationUseCase {

    private final NotificationPersistencePort notifRepo;
    private final NotificationPreferencePersistencePort prefRepo;
    private final ProjectNotificationSettingsPersistencePort settingsRepo;
    private final ProjectMemberPersistencePort memberRepo;
    private final SseNotificationPort sse;

    @Override
    public Mono<Void> createForProject(String projectId, NotificationEventType eventType,
                                        String message, Map<String, Object> payload) {
        return settingsRepo.findByProjectId(projectId)
                .defaultIfEmpty(defaultSettings(projectId))
                .filter(s -> s.isEventEnabled(eventType.getKey()))
                .flatMapMany(s -> memberRepo.findByProjectId(projectId))
                .flatMap(member ->
                    prefRepo.findByUserIdAndProjectId(member.getUserId(), projectId)
                            .defaultIfEmpty(defaultPreference(member.getUserId(), projectId))
                            .flatMap(pref -> {
                                if (!pref.shouldDeliverInApp(eventType.getKey())) {
                                    return Mono.empty();
                                }

                                Notification notif = Notification.builder()
                                        .id(UUID.randomUUID().toString())
                                        .userId(member.getUserId())
                                        .projectId(projectId)
                                        .type(eventType.getKey())
                                        .title(eventType.getTitle())
                                        .message(message)
                                        .icon(eventType.getIcon())
                                        .payload(payload)
                                        .read(false)
                                        .createdAt(Instant.now())
                                        .build();

                                return notifRepo.save(notif)
                                        .doOnSuccess(saved -> pushSse(projectId, eventType, message, payload));
                            })
                )
                .then()
                .doOnSuccess(v -> log.info("Notifications created for project={}, event={}",
                        projectId, eventType.getKey()))
                .onErrorResume(e -> {
                    log.error("Failed to create notifications for project={}, event={}: {}",
                            projectId, eventType.getKey(), e.getMessage());
                    return Mono.empty();
                });
    }

    // ── SSE push ─────────────────────────────────────────────────────

    private void pushSse(String projectId, NotificationEventType eventType,
                         String message, Map<String, Object> payload) {
        try {
            var ssePayload = new java.util.HashMap<>(payload);
            ssePayload.put("eventType", eventType.getKey());
            ssePayload.put("_title", eventType.getTitle());
            ssePayload.put("_message", message);
            sse.emit("project-" + projectId, eventType.getKey(), ssePayload);
        } catch (Exception e) {
            log.debug("SSE push skipped (no subscribers): {}", e.getMessage());
        }
    }

    // ── Defaults ─────────────────────────────────────────────────────

    private ProjectNotificationSettings defaultSettings(String projectId) {
        return ProjectNotificationSettings.builder()
                .projectId(projectId)
                .enabledEvents(NotificationEventType.allKeys())
                .build();
    }

    private NotificationPreference defaultPreference(String userId, String projectId) {
        return NotificationPreference.builder()
                .userId(userId)
                .projectId(projectId)
                .mutedEvents(new HashSet<>())
                .inAppEnabled(true)
                .emailEnabled(true)
                .build();
    }
}
