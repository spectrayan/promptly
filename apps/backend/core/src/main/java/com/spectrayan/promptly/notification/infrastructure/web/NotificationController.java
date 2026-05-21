package com.spectrayan.promptly.notification.infrastructure.web;

import com.spectrayan.promptly.infrastructure.in.web.api.NotificationsApi;
import com.spectrayan.promptly.infrastructure.in.web.dto.NotificationCountResponse;
import com.spectrayan.promptly.infrastructure.in.web.dto.NotificationEventTypeInfo;
import com.spectrayan.promptly.infrastructure.in.web.dto.NotificationListResponse;
import com.spectrayan.promptly.infrastructure.in.web.dto.NotificationPreferenceResponse;
import com.spectrayan.promptly.infrastructure.in.web.dto.NotificationResponse;
import com.spectrayan.promptly.infrastructure.in.web.dto.ProjectNotificationSettingsResponse;
import com.spectrayan.promptly.infrastructure.in.web.dto.UpdateNotificationPreferenceRequest;
import com.spectrayan.promptly.infrastructure.in.web.dto.UpdateProjectNotificationSettingsRequest;
import com.spectrayan.promptly.notification.application.port.in.ManageNotificationPreferencesUseCase;
import com.spectrayan.promptly.notification.application.port.in.QueryNotificationUseCase;
import com.spectrayan.promptly.notification.domain.model.Notification;
import com.spectrayan.promptly.notification.domain.model.NotificationEventType;
import com.spectrayan.promptly.notification.domain.model.NotificationPreference;
import com.spectrayan.promptly.notification.domain.model.ProjectNotificationSettings;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * REST controller for notification management.
 * Implements the contract-first {@link NotificationsApi} interface generated from the OpenAPI specification.
 * All endpoints extract the userId from the JWT principal via {@link ServerWebExchange}.
 */
@RestController
@RequiredArgsConstructor
public class NotificationController implements NotificationsApi {

    private final QueryNotificationUseCase queryNotificationUseCase;
    private final ManageNotificationPreferencesUseCase managePreferencesUseCase;

    // ── Notifications ──

    @Override
    public Mono<ResponseEntity<NotificationListResponse>> listNotifications(
            String projectId, Boolean unreadOnly,
            @Nullable OffsetDateTime before, Integer limit,
            ServerWebExchange exchange) {

        return exchange.getPrincipal().flatMap(principal -> {
            String userId = principal.getName();
            Instant cursor = before != null ? before.toInstant() : null;
            int fetchLimit = Math.min(limit != null ? limit : 20, 50);

            return queryNotificationUseCase.getByUserAndProject(userId, projectId, unreadOnly, cursor, fetchLimit + 1)
                    .collectList()
                    .map(items -> {
                        boolean hasMore = items.size() > fetchLimit;
                        List<Notification> page = hasMore ? items.subList(0, fetchLimit) : items;

                        var response = new NotificationListResponse();
                        response.setItems(page.stream().map(this::toNotificationResponse).toList());
                        response.setHasMore(hasMore);
                        if (!page.isEmpty()) {
                            response.setOldestTimestamp(toOffsetDateTime(page.get(page.size() - 1).getCreatedAt()));
                        }
                        return ResponseEntity.ok(response);
                    });
        });
    }

    @Override
    public Mono<ResponseEntity<NotificationCountResponse>> getUnreadNotificationCount(
            String projectId, ServerWebExchange exchange) {

        return exchange.getPrincipal().flatMap(principal -> {
            String userId = principal.getName();
            return queryNotificationUseCase.countUnread(userId, projectId)
                    .map(count -> {
                        var response = new NotificationCountResponse();
                        response.setUnread(count);
                        return ResponseEntity.ok(response);
                    });
        });
    }

    @Override
    public Mono<ResponseEntity<Void>> markNotificationAsRead(
            String id, ServerWebExchange exchange) {

        return exchange.getPrincipal().flatMap(principal ->
                queryNotificationUseCase.markAsRead(id, principal.getName())
                        .then(Mono.just(ResponseEntity.noContent().<Void>build())));
    }

    @Override
    public Mono<ResponseEntity<Void>> markAllNotificationsRead(
            String projectId, ServerWebExchange exchange) {

        return exchange.getPrincipal().flatMap(principal ->
                queryNotificationUseCase.markAllRead(principal.getName(), projectId)
                        .then(Mono.just(ResponseEntity.noContent().<Void>build())));
    }

    @Override
    public Mono<ResponseEntity<Void>> dismissNotification(
            String id, ServerWebExchange exchange) {

        return exchange.getPrincipal().flatMap(principal ->
                queryNotificationUseCase.dismiss(id, principal.getName())
                        .then(Mono.just(ResponseEntity.noContent().<Void>build())));
    }

    @Override
    public Mono<ResponseEntity<Void>> clearAllNotifications(
            String projectId, ServerWebExchange exchange) {

        return exchange.getPrincipal().flatMap(principal ->
                queryNotificationUseCase.clearAll(principal.getName(), projectId)
                        .then(Mono.just(ResponseEntity.noContent().<Void>build())));
    }

    // ── Preferences ──

    @Override
    public Mono<ResponseEntity<NotificationPreferenceResponse>> getNotificationPreferences(
            String projectId, ServerWebExchange exchange) {

        return exchange.getPrincipal().flatMap(principal ->
                managePreferencesUseCase.getPreferences(principal.getName(), projectId)
                        .map(pref -> ResponseEntity.ok(toPreferenceResponse(pref))));
    }

    @Override
    public Mono<ResponseEntity<NotificationPreferenceResponse>> updateNotificationPreferences(
            Mono<UpdateNotificationPreferenceRequest> request,
            ServerWebExchange exchange) {

        return exchange.getPrincipal().flatMap(principal ->
                request.flatMap(body -> {
                    NotificationPreference pref = NotificationPreference.builder()
                            .mutedEvents(new HashSet<>(body.getMutedEvents() != null ? body.getMutedEvents() : List.of()))
                            .inAppEnabled(body.getInAppEnabled() != null ? body.getInAppEnabled() : true)
                            .emailEnabled(body.getEmailEnabled() != null ? body.getEmailEnabled() : true)
                            .build();

                    return managePreferencesUseCase.updatePreferences(
                                    principal.getName(), body.getProjectId(), pref)
                            .map(saved -> ResponseEntity.ok(toPreferenceResponse(saved)));
                }));
    }

    // ── Project Settings (admin-level) ──

    @Override
    public Mono<ResponseEntity<ProjectNotificationSettingsResponse>> getProjectNotificationSettings(
            String projectId, ServerWebExchange exchange) {

        return managePreferencesUseCase.getProjectSettings(projectId)
                .map(s -> ResponseEntity.ok(toSettingsResponse(s)));
    }

    @Override
    public Mono<ResponseEntity<ProjectNotificationSettingsResponse>> updateProjectNotificationSettings(
            Mono<UpdateProjectNotificationSettingsRequest> request,
            ServerWebExchange exchange) {

        return request.flatMap(body -> {
            ProjectNotificationSettings settings = ProjectNotificationSettings.builder()
                    .enabledEvents(new HashSet<>(
                            body.getEnabledEvents() != null
                                    ? body.getEnabledEvents()
                                    : new ArrayList<>(NotificationEventType.allKeys())))
                    .build();

            return managePreferencesUseCase.updateProjectSettings(body.getProjectId(), settings)
                    .map(saved -> ResponseEntity.ok(toSettingsResponse(saved)));
        });
    }

    // ── Domain → DTO mapping ──────────────────────────────────────────

    private NotificationResponse toNotificationResponse(Notification n) {
        var dto = new NotificationResponse();
        dto.setId(n.getId());
        dto.setUserId(n.getUserId());
        dto.setProjectId(n.getProjectId());
        dto.setType(n.getType());
        dto.setTitle(n.getTitle());
        dto.setMessage(n.getMessage());
        dto.setIcon(n.getIcon());
        dto.setPayload(n.getPayload());
        dto.setRead(n.isRead());
        dto.setCreatedAt(toOffsetDateTime(n.getCreatedAt()));
        return dto;
    }

    private NotificationPreferenceResponse toPreferenceResponse(NotificationPreference p) {
        var dto = new NotificationPreferenceResponse();
        dto.setUserId(p.getUserId());
        dto.setProjectId(p.getProjectId());
        dto.setMutedEvents(p.getMutedEvents() != null ? new ArrayList<>(p.getMutedEvents()) : List.of());
        dto.setInAppEnabled(p.isInAppEnabled());
        dto.setEmailEnabled(p.isEmailEnabled());
        // Include all available event types for the UI toggle grid
        dto.setAvailableEvents(Arrays.stream(NotificationEventType.values())
                .map(this::toEventTypeInfo).toList());
        return dto;
    }

    private ProjectNotificationSettingsResponse toSettingsResponse(ProjectNotificationSettings s) {
        var dto = new ProjectNotificationSettingsResponse();
        dto.setProjectId(s.getProjectId());
        dto.setEnabledEvents(s.getEnabledEvents() != null ? new ArrayList<>(s.getEnabledEvents()) : List.of());
        dto.setAvailableEvents(Arrays.stream(NotificationEventType.values())
                .map(this::toEventTypeInfo).toList());
        return dto;
    }

    private NotificationEventTypeInfo toEventTypeInfo(NotificationEventType e) {
        var info = new NotificationEventTypeInfo();
        info.setKey(e.getKey());
        info.setTitle(e.getTitle());
        info.setIcon(e.getIcon());
        info.setMessageTemplate(e.getMessageTemplate());
        return info;
    }

    private OffsetDateTime toOffsetDateTime(Instant instant) {
        return instant != null ? instant.atOffset(ZoneOffset.UTC) : null;
    }
}
