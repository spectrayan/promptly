package com.promptly.shared.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.WebFilter;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Configurable rate limiting using Resilience4j.
 * <p>
 * Enable/disable via {@code promptly.rate-limit.enabled=true|false}.
 * Disable if rate limiting is handled at the API gateway / nginx / cloud LB level.
 * <p>
 * Configuration example:
 * <pre>
 * promptly:
 *   rate-limit:
 *     enabled: true
 *     requests-per-second: 100       # global per-IP limit
 *     login-requests-per-minute: 5   # stricter limit for auth endpoints
 *     timeout: 0                     # wait time for a permit (0 = fail immediately)
 * </pre>
 */
@Configuration
@ConditionalOnProperty(name = "promptly.rate-limit.enabled", havingValue = "true")
public class RateLimitConfig {

    private static final Logger log = LoggerFactory.getLogger(RateLimitConfig.class);

    @Value("${promptly.rate-limit.requests-per-second:100}")
    private int requestsPerSecond;

    @Value("${promptly.rate-limit.login-requests-per-minute:5}")
    private int loginRequestsPerMinute;

    @Value("${promptly.rate-limit.timeout:0}")
    private int timeoutMs;

    @Bean
    public RateLimiterRegistry rateLimiterRegistry() {
        return RateLimiterRegistry.ofDefaults();
    }

    @Bean
    public RateLimiterConfig globalRateLimiterConfig() {
        return RateLimiterConfig.custom()
                .limitForPeriod(requestsPerSecond)
                .limitRefreshPeriod(Duration.ofSeconds(1))
                .timeoutDuration(Duration.ofMillis(timeoutMs))
                .build();
    }

    @Bean
    public RateLimiterConfig loginRateLimiterConfig() {
        return RateLimiterConfig.custom()
                .limitForPeriod(loginRequestsPerMinute)
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .timeoutDuration(Duration.ofMillis(timeoutMs))
                .build();
    }

    @Bean
    public WebFilter rateLimitFilter(RateLimiterRegistry registry,
                                     RateLimiterConfig globalRateLimiterConfig,
                                     RateLimiterConfig loginRateLimiterConfig) {
        log.info("Rate limiting enabled (Resilience4j): {} req/s global, {} req/min login",
                requestsPerSecond, loginRequestsPerMinute);

        var perIpLimiters = new ConcurrentHashMap<String, RateLimiter>();
        var perIpLoginLimiters = new ConcurrentHashMap<String, RateLimiter>();

        return (exchange, chain) -> {
            String path = exchange.getRequest().getPath().value();

            // Skip non-API paths (actuator, static, etc.)
            if (!path.startsWith("/api/")) {
                return chain.filter(exchange);
            }

            String clientIp = resolveClientIp(exchange.getRequest().getRemoteAddress());

            // Stricter rate limit for login/register
            if (path.contains("/auth/login") || path.contains("/auth/register")) {
                RateLimiter loginLimiter = perIpLoginLimiters.computeIfAbsent(
                        "login-" + clientIp,
                        key -> registry.rateLimiter(key, loginRateLimiterConfig));

                if (!loginLimiter.acquirePermission()) {
                    log.warn("Login rate limit exceeded for IP: {}", clientIp);
                    exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                    exchange.getResponse().getHeaders().set("Retry-After", "60");
                    return exchange.getResponse().setComplete();
                }
            }

            // Global per-IP rate limit
            RateLimiter globalLimiter = perIpLimiters.computeIfAbsent(
                    "global-" + clientIp,
                    key -> registry.rateLimiter(key, globalRateLimiterConfig));

            if (!globalLimiter.acquirePermission()) {
                exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                exchange.getResponse().getHeaders().set("Retry-After", "1");
                return exchange.getResponse().setComplete();
            }

            return chain.filter(exchange);
        };
    }

    private String resolveClientIp(InetSocketAddress remoteAddress) {
        if (remoteAddress == null || remoteAddress.getAddress() == null) {
            return "unknown";
        }
        return remoteAddress.getAddress().getHostAddress();
    }
}
