package com.spectrayan.promptly.notification.application.port.in;

import com.spectrayan.promptly.notification.domain.model.NotificationPreference;
import com.spectrayan.promptly.notification.domain.model.ProjectNotificationSettings;
import reactor.core.publisher.Mono;

/**
 * Inbound port for managing notification preferences and project-level settings.
 */
public interface ManageNotificationPreferencesUseCase {

    // ── User Preferences ──

    Mono<NotificationPreference> getPreferences(String userId, String projectId);

    Mono<NotificationPreference> updatePreferences(String userId, String projectId,
                                                    NotificationPreference preference);

    // ── Project Settings ──

    Mono<ProjectNotificationSettings> getProjectSettings(String projectId);

    Mono<ProjectNotificationSettings> updateProjectSettings(String projectId,
                                                             ProjectNotificationSettings settings);
}
