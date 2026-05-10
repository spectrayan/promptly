package com.promptly.shared.config;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Enables async processing for event listeners using <b>virtual threads</b>.
 * <p>
 * In this WebFlux application, the {@code @Async} listeners do not perform blocking I/O
 * themselves — they assemble a reactive pipeline and call {@code .subscribe()}, which
 * returns immediately.  The virtual thread acts as a <b>lightweight trampoline</b> that
 * decouples the event publisher's thread (often a Netty event-loop thread) from the
 * listener, preventing synchronous listener invocation from stalling the publisher's
 * reactive chain.
 * <p>
 * Virtual threads (Project Loom, Java 21+) are ideal for this: they are created on-demand,
 * live for only microseconds (just long enough to call {@code subscribe()}), and are
 * garbage-collected automatically — no fixed pool or idle-timeout tuning required.
 * <p>
 * The configured executor also propagates SLF4J MDC context (e.g., traceId) from
 * the caller thread to the virtual thread so that log correlation works correctly.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Default {@link Executor} for all {@code @Async} methods.
     * Uses {@link VirtualThreadTaskExecutor} so that every task runs on a
     * dedicated virtual thread.  In this reactive codebase, listeners call
     * {@code .subscribe()} and return instantly — the virtual thread lives
     * for microseconds and is then garbage-collected.  No fixed pool, no
     * idle timeouts to configure.
     */
    @Bean
    public Executor taskExecutor() {
        var executor = new VirtualThreadTaskExecutor("promptly-async-");
        return new MdcPropagatingExecutor(executor);
    }

    /**
     * Wraps an executor to propagate SLF4J MDC context across thread boundaries.
     * Without this, virtual threads lose traceId / requestId from the parent context.
     */
    private record MdcPropagatingExecutor(Executor delegate) implements Executor {

        @Override
        public void execute(Runnable command) {
            Map<String, String> callerMdc = MDC.getCopyOfContextMap();
            delegate.execute(() -> {
                if (callerMdc != null) {
                    MDC.setContextMap(callerMdc);
                }
                try {
                    command.run();
                } finally {
                    MDC.clear();
                }
            });
        }
    }
}
