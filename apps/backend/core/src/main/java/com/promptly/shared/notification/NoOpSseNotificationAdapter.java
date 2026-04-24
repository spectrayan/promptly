package com.promptly.shared.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * No-op SSE notification adapter for MVP.
 * <p>
 * Logs emissions at DEBUG level. When the SSE server library
 * (from spectrayan-health) is integrated, this will be replaced
 * with a real adapter backed by {@code SseEmitter}.
 * <p>
 * This ensures domain services can emit events now without waiting
 * for SSE infrastructure.
 */
@Slf4j
@Component
public class NoOpSseNotificationAdapter implements SseNotificationPort {

    @Override
    public <T> void emit(String topic, String eventName, T payload) {
        log.debug("SSE emit (no-op): topic={}, event={}, payload={}", topic, eventName, payload);
    }

    @Override
    public <T> void emit(String topic, T payload) {
        log.debug("SSE emit (no-op): topic={}, payload={}", topic, payload);
    }

    @Override
    public <T> void broadcast(String eventName, T payload) {
        log.debug("SSE broadcast (no-op): event={}, payload={}", eventName, payload);
    }
}
