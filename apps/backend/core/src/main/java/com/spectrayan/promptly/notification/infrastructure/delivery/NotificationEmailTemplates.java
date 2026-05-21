package com.spectrayan.promptly.notification.infrastructure.delivery;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads and resolves email HTML templates from classpath resources.
 * <p>
 * Templates live under {@code templates/email/} and use {@code {{key}}}
 * placeholders that are replaced with escaped values at render time.
 * <p>
 * Templates are cached after first load for performance.
 */
@Slf4j
@Component
public class NotificationEmailTemplates {

    private static final String TEMPLATE_DIR = "templates/email/";
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    /**
     * Renders the workflow-approved email template.
     */
    public String approved(String promptName, String approvedBy) {
        return resolve("workflow-approved.html", Map.of(
                "promptName", escape(promptName),
                "approvedBy", escape(approvedBy)
        ));
    }

    /**
     * Renders the workflow-rejected email template.
     */
    public String rejected(String promptName, String rejectedBy, String reason) {
        String safeReason = (reason != null && !reason.isBlank()) ? reason : "No reason provided";
        return resolve("workflow-rejected.html", Map.of(
                "promptName", escape(promptName),
                "rejectedBy", escape(rejectedBy),
                "reason", escape(safeReason)
        ));
    }

    // ── Internal ─────────────────────────────────────────────────────

    /**
     * Loads a template from classpath (cached) and replaces all {{key}} placeholders.
     */
    private String resolve(String templateName, Map<String, String> variables) {
        String template = loadTemplate(templateName);
        String result = template;
        for (var entry : variables.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return result;
    }

    /**
     * Loads template content from classpath, caching on first access.
     */
    private String loadTemplate(String templateName) {
        return cache.computeIfAbsent(templateName, name -> {
            try {
                var resource = new ClassPathResource(TEMPLATE_DIR + name);
                return resource.getContentAsString(StandardCharsets.UTF_8);
            } catch (IOException e) {
                log.error("Failed to load email template '{}': {}", name, e.getMessage());
                throw new IllegalStateException("Email template not found: " + name, e);
            }
        });
    }

    /**
     * Basic HTML escaping to prevent XSS in email bodies.
     */
    private String escape(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;");
    }
}
