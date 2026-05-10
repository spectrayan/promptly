package com.spectrayan.promptly.notification.infrastructure.delivery;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link NotificationEmailTemplates}.
 * Verifies that HTML templates are loaded from classpath, placeholders
 * are resolved, and XSS escaping is applied.
 */
@DisplayName("NotificationEmailTemplates")
class NotificationEmailTemplatesTest {

    private final NotificationEmailTemplates templates = new NotificationEmailTemplates();

    @Test
    @DisplayName("approved template should contain prompt name and approver")
    void approvedShouldContainDetails() {
        String html = templates.approved("Login Helper", "alice@promptly.ai");

        assertThat(html)
                .contains("Login Helper")
                .contains("alice@promptly.ai")
                .contains("approved")
                .contains("<!DOCTYPE html>");
    }

    @Test
    @DisplayName("rejected template should contain prompt name, rejector, and reason")
    void rejectedShouldContainDetails() {
        String html = templates.rejected("Login Helper", "bob@promptly.ai", "Needs better context");

        assertThat(html)
                .contains("Login Helper")
                .contains("bob@promptly.ai")
                .contains("Needs better context")
                .contains("rejected");
    }

    @Test
    @DisplayName("rejected template should show fallback when reason is null")
    void rejectedShouldHandleNullReason() {
        String html = templates.rejected("Test Prompt", "carol", null);

        assertThat(html).contains("No reason provided");
    }

    @Test
    @DisplayName("rejected template should show fallback when reason is blank")
    void rejectedShouldHandleBlankReason() {
        String html = templates.rejected("Test Prompt", "carol", "   ");

        assertThat(html).contains("No reason provided");
    }

    @Test
    @DisplayName("should HTML-escape special characters to prevent XSS")
    void shouldEscapeXss() {
        String html = templates.approved("<script>alert('xss')</script>", "attacker");

        assertThat(html)
                .doesNotContain("<script>")
                .contains("&lt;script&gt;");
    }
}
