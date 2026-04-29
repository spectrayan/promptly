package com.promptly.shared.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Validates critical production configuration on startup.
 * Fails fast if required secrets are missing or insecure defaults are detected.
 */
@Configuration
@Profile("prod")
public class ProductionConfigValidator {

    private static final Logger log = LoggerFactory.getLogger(ProductionConfigValidator.class);

    private final PromptlyProperties properties;

    @Value("${spring.mongodb.uri:}")
    private String mongoUri;

    public ProductionConfigValidator(PromptlyProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void validate() {
        var errors = new java.util.ArrayList<String>();

        String jwtSecret = properties.getAuth().getJwt().getSecret();

        // JWT secret must be set and not a known dev default
        if (jwtSecret == null || jwtSecret.isBlank()) {
            errors.add("JWT_SECRET environment variable is required in production");
        } else if (jwtSecret.contains("DO-NOT-USE") || jwtSecret.contains("dev-secret") || jwtSecret.length() < 32) {
            errors.add("JWT_SECRET must be a secure random string (min 32 chars) — do not use dev defaults");
        }

        // MongoDB URI must be set
        if (mongoUri == null || mongoUri.isBlank()) {
            errors.add("MONGODB_URI environment variable is required in production");
        } else if (mongoUri.contains("localhost")) {
            log.warn("MONGODB_URI points to localhost — ensure this is intentional for production");
        }

        if (!errors.isEmpty()) {
            var msg = "\n\n" +
                    "╔══════════════════════════════════════════════════════════╗\n" +
                    "║  PRODUCTION CONFIGURATION ERROR                        ║\n" +
                    "╠══════════════════════════════════════════════════════════╣\n";
            for (String err : errors) {
                msg += "║  ✗ " + err + "\n";
            }
            msg += "╚══════════════════════════════════════════════════════════╝\n";
            throw new IllegalStateException(msg);
        }

        log.info("Production configuration validated successfully");
    }
}
