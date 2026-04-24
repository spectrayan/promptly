package com.promptly.shared.notification;

import java.util.List;

/**
 * Outbound port for sending email notifications.
 * <p>
 * Uses Spring's {@code JavaMailSender} under the hood (if SMTP is configured).
 * If no SMTP is available, implementations should silently skip — email is best-effort.
 */
public interface EmailNotificationPort {

    /**
     * Send an HTML email to a single recipient.
     *
     * @param to      recipient email address
     * @param subject email subject line
     * @param htmlBody HTML email body
     */
    void send(String to, String subject, String htmlBody);

    /**
     * Send an HTML email to multiple recipients.
     */
    void send(List<String> to, String subject, String htmlBody);
}
