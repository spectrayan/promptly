package com.promptly.shared.notification;

import com.spectrayan.sse.server.emitter.SseEmitter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Real SSE notification adapter backed by the Spectrayan SSE Server library.
 * <p>
 * Replaces {@link NoOpSseNotificationAdapter} via {@code @Primary}.
 * Delegates to the library's {@link SseEmitter} for topic-based event delivery.
 */
@Slf4j
@Primary
@Component
@RequiredArgsConstructor
public class SseNotificationAdapter implements SseNotificationPort {

    private final SseEmitter emitter;

    @Override
    public <T> void emit(String topic, String eventName, T payload) {
        try {
            emitter.emit(topic, eventName, payload);
        } catch (Exception e) {
            log.debug("SSE: No subscribers for topic '{}', event '{}' dropped", topic, eventName);
        }
    }

    @Override
    public <T> void emit(String topic, T payload) {
        try {
            emitter.emit(topic, payload);
        } catch (Exception e) {
            log.debug("SSE: No subscribers for topic '{}', event dropped", topic);
        }
    }

    @Override
    public <T> void broadcast(String eventName, T payload) {
        try {
            emitter.emitToAll(payload);
        } catch (Exception e) {
            log.warn("SSE: Broadcast failed for event '{}': {}", eventName, e.getMessage());
        }
    }
}
