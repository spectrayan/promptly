package com.promptly.notification.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link NotificationEventType} template resolution.
 */
@DisplayName("NotificationEventType")
class NotificationEventTypeTest {

    @Test
    @DisplayName("resolveMessage should substitute all placeholders")
    void shouldResolveAllPlaceholders() {
        String result = NotificationEventType.PROMPT_CREATED.resolveMessage(
                Map.of("promptName", "My Prompt", "promptId", "p-1")
        );

        assertThat(result).isEqualTo("Prompt \"My Prompt\" was created");
    }

    @Test
    @DisplayName("resolveMessage should handle missing keys gracefully")
    void shouldHandleMissingKeys() {
        String result = NotificationEventType.PROMPT_CREATED.resolveMessage(
                Map.of("promptId", "p-1")
        );

        // {promptName} should be removed, leaving clean text
        assertThat(result).doesNotContain("{promptName}");
        assertThat(result).contains("was created");
    }

    @Test
    @DisplayName("resolveMessage should handle null payload")
    void shouldHandleNullPayload() {
        String result = NotificationEventType.PROMPT_UPDATED.resolveMessage(null);

        assertThat(result).isNotNull();
        assertThat(result).doesNotContain("{");
    }

    @Test
    @DisplayName("resolveMessage for WORKFLOW_REJECTED should include reason")
    void shouldResolveRejectionWithReason() {
        String result = NotificationEventType.WORKFLOW_REJECTED.resolveMessage(
                Map.of("rejectedBy", "carol", "reason", "Needs more context")
        );

        assertThat(result).contains("carol");
        assertThat(result).contains("Needs more context");
    }

    @Test
    @DisplayName("resolveMessage for WORKFLOW_REJECTED should clean up empty reason")
    void shouldCleanUpEmptyReason() {
        String result = NotificationEventType.WORKFLOW_REJECTED.resolveMessage(
                Map.of("rejectedBy", "carol", "reason", "")
        );

        assertThat(result).contains("carol");
        assertThat(result).doesNotEndWith(":");
    }

    @Test
    @DisplayName("resolveMessage for version should handle numeric values")
    void shouldHandleNumericValues() {
        String result = NotificationEventType.PROMPT_UPDATED.resolveMessage(
                Map.of("version", 5, "promptName", "Test Prompt")
        );

        assertThat(result).isEqualTo("Prompt \"Test Prompt\" updated to version 5");
    }

    @Test
    @DisplayName("allKeys should return all event type keys")
    void shouldReturnAllKeys() {
        var keys = NotificationEventType.allKeys();

        assertThat(keys).containsExactlyInAnyOrder(
                "prompt.created", "prompt.updated",
                "workflow.approved", "workflow.rejected",
                "scan.completed", "scan.critical"
        );
    }

    @Test
    @DisplayName("fromKey should find existing type")
    void shouldFindByKey() {
        assertThat(NotificationEventType.fromKey("workflow.approved"))
                .isEqualTo(NotificationEventType.WORKFLOW_APPROVED);
    }

    @Test
    @DisplayName("fromKey should return null for unknown key")
    void shouldReturnNullForUnknownKey() {
        assertThat(NotificationEventType.fromKey("unknown.event")).isNull();
    }
}
