package com.promptly.notification.application.service;

import com.promptly.notification.application.port.in.NotificationUseCase;
import com.promptly.notification.application.port.out.NotificationPreferenceRepository;
import com.promptly.notification.application.port.out.NotificationRepository;
import com.promptly.notification.application.port.out.ProjectNotificationSettingsRepository;
import com.promptly.notification.domain.model.Notification;
import com.promptly.notification.domain.model.NotificationEventType;
import com.promptly.notification.domain.model.NotificationPreference;
import com.promptly.notification.domain.model.ProjectNotificationSettings;
import com.promptly.project.application.port.out.ProjectMemberRepository;
import com.promptly.shared.notification.SseNotificationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

/**
 * Orchestrates notification lifecycle:
 * - Fan-out creation for project members (respecting settings + preferences)
 * - Cursor-based queries
 * - Read/dismiss mutations
 * - Preference and project settings management
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationApplicationService implements NotificationUseCase {

    private final NotificationRepository notifRepo;
    private final NotificationPreferenceRepository prefRepo;
    private final ProjectNotificationSettingsRepository settingsRepo;
    private final ProjectMemberRepository memberRepo;
    private final SseNotificationPort sse;

    // ── Fan-out creation ──

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
                                        .doOnSuccess(saved -> {
                                            // SSE push for real-time delivery
                                            // Include eventType in payload so frontend can build proper messages
                                            try {
                                                var ssePayload = new java.util.HashMap<>(payload);
                                                ssePayload.put("eventType", eventType.getKey());
                                                ssePayload.put("_title", eventType.getTitle());
                                                ssePayload.put("_message", message);
                                                sse.emit("project-" + projectId,
                                                        eventType.getKey(), ssePayload);
                                            } catch (Exception e) {
                                                log.debug("SSE push skipped (no subscribers): {}",
                                                        e.getMessage());
                                            }
                                        });
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

    // ── Query ──

    @Override
    public Flux<Notification> getByUserAndProject(String userId, String projectId,
                                                   boolean unreadOnly, Instant before, int limit) {
        return notifRepo.findByUserAndProject(userId, projectId, unreadOnly, before, limit);
    }

    @Override
    public Mono<Long> countUnread(String userId, String projectId) {
        return notifRepo.countByUserIdAndProjectIdAndReadFalse(userId, projectId);
    }

    // ── Mutations ──

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

    // ── Preferences ──

    @Override
    public Mono<NotificationPreference> getPreferences(String userId, String projectId) {
        return prefRepo.findByUserIdAndProjectId(userId, projectId)
                .defaultIfEmpty(defaultPreference(userId, projectId));
    }

    @Override
    public Mono<NotificationPreference> updatePreferences(String userId, String projectId,
                                                           NotificationPreference preference) {
        return prefRepo.findByUserIdAndProjectId(userId, projectId)
                .map(existing -> {
                    existing.setMutedEvents(preference.getMutedEvents());
                    existing.setInAppEnabled(preference.isInAppEnabled());
                    existing.setEmailEnabled(preference.isEmailEnabled());
                    return existing;
                })
                .switchIfEmpty(Mono.defer(() -> {
                    preference.setUserId(userId);
                    preference.setProjectId(projectId);
                    return Mono.just(preference);
                }))
                .flatMap(prefRepo::save);
    }

    // ── Project Settings ──

    @Override
    public Mono<ProjectNotificationSettings> getProjectSettings(String projectId) {
        return settingsRepo.findByProjectId(projectId)
                .defaultIfEmpty(defaultSettings(projectId));
    }

    @Override
    public Mono<ProjectNotificationSettings> updateProjectSettings(String projectId,
                                                                     ProjectNotificationSettings settings) {
        return settingsRepo.findByProjectId(projectId)
                .map(existing -> {
                    existing.setEnabledEvents(settings.getEnabledEvents());
                    return existing;
                })
                .switchIfEmpty(Mono.defer(() -> {
                    settings.setProjectId(projectId);
                    return Mono.just(settings);
                }))
                .flatMap(settingsRepo::save);
    }

    // ── Defaults ──

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
