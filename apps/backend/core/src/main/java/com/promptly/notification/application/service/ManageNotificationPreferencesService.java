package com.promptly.notification.application.service;

import com.promptly.notification.application.port.in.ManageNotificationPreferencesUseCase;
import com.promptly.notification.application.port.out.NotificationPreferencePersistencePort;
import com.promptly.notification.application.port.out.ProjectNotificationSettingsPersistencePort;
import com.promptly.notification.domain.model.NotificationEventType;
import com.promptly.notification.domain.model.NotificationPreference;
import com.promptly.notification.domain.model.ProjectNotificationSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashSet;

/**
 * Application service for managing notification preferences and project-level settings.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ManageNotificationPreferencesService implements ManageNotificationPreferencesUseCase {

    private final NotificationPreferencePersistencePort prefRepo;
    private final ProjectNotificationSettingsPersistencePort settingsRepo;

    // ── User Preferences ─────────────────────────────────────────────

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

    // ── Project Settings ─────────────────────────────────────────────

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
