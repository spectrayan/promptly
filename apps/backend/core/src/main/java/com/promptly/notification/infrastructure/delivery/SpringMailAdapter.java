package com.promptly.notification.infrastructure.delivery;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Spring Mail adapter implementing the {@link EmailNotificationPort}.
 * <p>
 * Uses {@link ObjectProvider} for the mail sender — if SMTP is not configured,
 * the bean won't exist and emails are silently skipped. All exceptions are caught
 * and logged, never thrown. Email is a best-effort notification channel.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SpringMailAdapter implements EmailNotificationPort {

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Override
    public void send(String to, String subject, String htmlBody) {
        var sender = mailSenderProvider.getIfAvailable();
        if (sender == null) {
            log.debug("Email skipped (no mail sender configured): to={}, subject={}", to, subject);
            return;
        }
        try {
            var msg = sender.createMimeMessage();
            var helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            sender.send(msg);
            log.info("Email sent: to={}, subject={}", to, subject);
        } catch (Exception e) {
            log.warn("Email send failed (non-fatal): to={}, subject={}, error={}",
                    to, subject, e.getMessage());
        }
    }

    @Override
    public void send(List<String> to, String subject, String htmlBody) {
        var sender = mailSenderProvider.getIfAvailable();
        if (sender == null) {
            log.debug("Email skipped (no mail sender configured): to={}, subject={}", to, subject);
            return;
        }
        try {
            var msg = sender.createMimeMessage();
            var helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setTo(to.toArray(String[]::new));
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            sender.send(msg);
            log.info("Email sent: to={}, subject={}", to, subject);
        } catch (Exception e) {
            log.warn("Email send failed (non-fatal): to={}, subject={}, error={}",
                    to, subject, e.getMessage());
        }
    }
}
