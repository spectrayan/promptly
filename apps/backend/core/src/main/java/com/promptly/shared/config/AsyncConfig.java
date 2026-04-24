package com.promptly.shared.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Enables async processing for event listeners.
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}
