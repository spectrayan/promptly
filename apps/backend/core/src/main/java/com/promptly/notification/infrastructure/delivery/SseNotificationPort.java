package com.promptly.notification.infrastructure.delivery;

/**
 * Outbound port for emitting Server-Sent Events (SSE) notifications to connected UI clients.
 * <p>
 * This port abstracts the SSE infrastructure so that domain/application services
 * never depend directly on the SSE server library. Infrastructure adapters implement
 * this interface using the SSE emitter.
 * <p>
 * Topic naming conventions:
 * <ul>
 *   <li>{@code user-{userId}} — notifications scoped to a single user</li>
 *   <li>{@code project-{projectId}} — notifications for all members of a project</li>
 * </ul>
 */
public interface SseNotificationPort {

    /**
     * Emit an SSE event to a specific topic.
     *
     * @param topic     the topic identifier (e.g. "user-abc123")
     * @param eventName the SSE event name (e.g. "notification", "scan.completed")
     * @param payload   the payload object to serialize and send
     * @param <T>       the payload type
     */
    <T> void emit(String topic, String eventName, T payload);

    /**
     * Emit an SSE event to a specific topic without a named event type.
     * The event will use the default "message" event type.
     */
    <T> void emit(String topic, T payload);

    /**
     * Broadcast an SSE event to all currently connected topics.
     */
    <T> void broadcast(String eventName, T payload);
}
