package com.promptly.notification.infrastructure.web;

import com.promptly.notification.application.port.in.NotificationUseCase;
import com.promptly.notification.domain.model.Notification;
import com.promptly.notification.domain.model.NotificationEventType;
import com.promptly.notification.domain.model.NotificationPreference;
import com.promptly.notification.domain.model.ProjectNotificationSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.time.Instant;
import java.util.*;

/**
 * REST controller for notification management.
 * All endpoints extract the userId from the JWT principal.
 */
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationUseCase notificationUseCase;

    /**
     * Cursor-based notification list.
     * GET /api/v1/notifications?projectId=X&unreadOnly=true&before=ISO&limit=20
     */
    @GetMapping
    public Mono<ResponseEntity<Map<String, Object>>> listNotifications(
            Principal principal,
            @RequestParam String projectId,
            @RequestParam(defaultValue = "false") boolean unreadOnly,
            @RequestParam(required = false) String before,
            @RequestParam(defaultValue = "20") int limit) {

        String userId = principal.getName();
        Instant cursor = before != null ? Instant.parse(before) : null;
        int fetchLimit = Math.min(limit, 50); // Cap at 50

        return notificationUseCase.getByUserAndProject(userId, projectId, unreadOnly, cursor, fetchLimit + 1)
                .collectList()
                .map(items -> {
                    boolean hasMore = items.size() > fetchLimit;
                    List<Notification> page = hasMore ? items.subList(0, fetchLimit) : items;

                    Map<String, Object> response = new LinkedHashMap<>();
                    response.put("items", page.stream().map(this::toResponse).toList());
                    response.put("hasMore", hasMore);
                    if (!page.isEmpty()) {
                        response.put("oldestTimestamp", page.get(page.size() - 1).getCreatedAt().toString());
                    }
                    return ResponseEntity.ok(response);
                });
    }

    /**
     * Unread count for the bell badge.
     * GET /api/v1/notifications/count?projectId=X
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Map<String, Long>>> unreadCount(
            Principal principal,
            @RequestParam String projectId) {

        String userId = principal.getName();
        return notificationUseCase.countUnread(userId, projectId)
                .map(count -> ResponseEntity.ok(Map.of("unread", count)));
    }

    /**
     * Mark a single notification as read.
     * PATCH /api/v1/notifications/{id}/read
     */
    @PatchMapping("/{id}/read")
    public Mono<ResponseEntity<Void>> markAsRead(
            Principal principal,
            @PathVariable String id) {

        return notificationUseCase.markAsRead(id, principal.getName())
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }

    /**
     * Mark all notifications as read for a project.
     * POST /api/v1/notifications/mark-all-read?projectId=X
     */
    @PostMapping("/mark-all-read")
    public Mono<ResponseEntity<Void>> markAllRead(
            Principal principal,
            @RequestParam String projectId) {

        return notificationUseCase.markAllRead(principal.getName(), projectId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }

    /**
     * Dismiss (delete) a notification.
     * DELETE /api/v1/notifications/{id}
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> dismiss(
            Principal principal,
            @PathVariable String id) {

        return notificationUseCase.dismiss(id, principal.getName())
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }

    // ── Preferences ──

    /**
     * Get current user's notification preferences for a project.
     * GET /api/v1/notifications/preferences?projectId=X
     */
    @GetMapping("/preferences")
    public Mono<ResponseEntity<Map<String, Object>>> getPreferences(
            Principal principal,
            @RequestParam String projectId) {

        return notificationUseCase.getPreferences(principal.getName(), projectId)
                .map(pref -> ResponseEntity.ok(prefToResponse(pref)));
    }

    /**
     * Update notification preferences.
     * PUT /api/v1/notifications/preferences
     */
    @PutMapping("/preferences")
    public Mono<ResponseEntity<Map<String, Object>>> updatePreferences(
            Principal principal,
            @RequestBody Map<String, Object> body) {

        String projectId = (String) body.get("projectId");
        @SuppressWarnings("unchecked")
        List<String> mutedList = (List<String>) body.getOrDefault("mutedEvents", List.of());
        boolean inAppEnabled = (boolean) body.getOrDefault("inAppEnabled", true);
        boolean emailEnabled = (boolean) body.getOrDefault("emailEnabled", true);

        NotificationPreference pref = NotificationPreference.builder()
                .mutedEvents(new HashSet<>(mutedList))
                .inAppEnabled(inAppEnabled)
                .emailEnabled(emailEnabled)
                .build();

        return notificationUseCase.updatePreferences(principal.getName(), projectId, pref)
                .map(saved -> ResponseEntity.ok(prefToResponse(saved)));
    }

    // ── Project Settings (admin-level) ──

    /**
     * GET /api/v1/notifications/project-settings?projectId=X
     */
    @GetMapping("/project-settings")
    public Mono<ResponseEntity<Map<String, Object>>> getProjectSettings(
            @RequestParam String projectId) {

        return notificationUseCase.getProjectSettings(projectId)
                .map(s -> ResponseEntity.ok(settingsToResponse(s)));
    }

    /**
     * PUT /api/v1/notifications/project-settings
     */
    @PutMapping("/project-settings")
    public Mono<ResponseEntity<Map<String, Object>>> updateProjectSettings(
            @RequestBody Map<String, Object> body) {

        String projectId = (String) body.get("projectId");
        @SuppressWarnings("unchecked")
        List<String> enabledList = (List<String>) body.getOrDefault("enabledEvents",
                new ArrayList<>(NotificationEventType.allKeys()));

        ProjectNotificationSettings settings = ProjectNotificationSettings.builder()
                .enabledEvents(new HashSet<>(enabledList))
                .build();

        return notificationUseCase.updateProjectSettings(projectId, settings)
                .map(saved -> ResponseEntity.ok(settingsToResponse(saved)));
    }

    // ── Response Mapping ──

    private Map<String, Object> toResponse(Notification n) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", n.getId());
        map.put("userId", n.getUserId());
        map.put("projectId", n.getProjectId());
        map.put("type", n.getType());
        map.put("title", n.getTitle());
        map.put("message", n.getMessage());
        map.put("icon", n.getIcon());
        map.put("payload", n.getPayload());
        map.put("read", n.isRead());
        map.put("createdAt", n.getCreatedAt() != null ? n.getCreatedAt().toString() : null);
        return map;
    }

    private Map<String, Object> prefToResponse(NotificationPreference p) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("userId", p.getUserId());
        map.put("projectId", p.getProjectId());
        map.put("mutedEvents", p.getMutedEvents());
        map.put("inAppEnabled", p.isInAppEnabled());
        map.put("emailEnabled", p.isEmailEnabled());
        // Include all available event types for the UI toggle grid
        map.put("availableEvents", NotificationEventType.values());
        return map;
    }

    private Map<String, Object> settingsToResponse(ProjectNotificationSettings s) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("projectId", s.getProjectId());
        map.put("enabledEvents", s.getEnabledEvents());
        map.put("availableEvents", NotificationEventType.values());
        return map;
    }
}
