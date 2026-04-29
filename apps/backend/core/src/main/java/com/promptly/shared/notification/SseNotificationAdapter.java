package com.promptly.shared.notification;

import com.spectrayan.sse.server.emitter.SseEmitter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.scheduler.Schedulers;

/**
 * Real SSE notification adapter backed by the Spectrayan SSE Server library.
 * <p>
 * Replaces {@link NoOpSseNotificationAdapter} via {@code @Primary}.
 * Delegates to the library's {@link SseEmitter} for topic-based event delivery.
 * <p>
 * SSE push is <strong>best-effort</strong>. Notifications are already persisted
 * before this adapter is called. If no subscriber is connected to the topic,
 * the event is silently dropped — the frontend's polling fallback and
 * initial-load-on-connect guarantee eventual delivery.
 * <p>
 * Uses {@link SseEmitter#emitReactive(String, String, Object)} to avoid
 * {@code FAIL_NON_SERIALIZED} errors when the REPLAY sink receives concurrent
 * emissions from different Netty event-loop threads.
 */
@Slf4j
@Primary
@Component
@RequiredArgsConstructor
public class SseNotificationAdapter implements SseNotificationPort {

    private final SseEmitter emitter;

    @Override
    public <T> void emit(String topic, String eventName, T payload) {
        // Check if any subscriber is listening on this topic before attempting emit.
        // This avoids triggering the EmissionService's exception path for topics
        // that don't exist yet (e.g. during app startup before any frontend connects).
        if (!emitter.currentTopics().contains(topic)) {
            log.trace("SSE: Topic '{}' has no subscribers, event '{}' skipped (persisted in DB)",
                    topic, eventName);
            return;
        }
        // Use emitReactive to avoid FAIL_NON_SERIALIZED on REPLAY sinks.
        // Subscribe on boundedElastic to offload from the calling event-loop thread.
        emitter.emitReactive(topic, eventName, payload)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(
                        unused -> {},
                        err -> log.debug("SSE: Failed to emit '{}' to topic '{}': {}",
                                eventName, topic, err.getMessage()),
                        () -> log.trace("SSE: Emitted '{}' to topic '{}'", eventName, topic)
                );
    }

    @Override
    public <T> void emit(String topic, T payload) {
        if (!emitter.currentTopics().contains(topic)) {
            log.trace("SSE: Topic '{}' has no subscribers, event skipped (persisted in DB)", topic);
            return;
        }
        emitter.emitReactive(topic, payload)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(
                        unused -> {},
                        err -> log.debug("SSE: Failed to emit to topic '{}': {}", topic, err.getMessage())
                );
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
