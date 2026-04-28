# Promptly — Production Readiness Audit

> **Verdict: NOT production-ready.** There are **6 critical** and **9 high-severity** gaps that must be addressed before deploying to production.

---

## Scorecard

| Dimension | Score | Verdict |
|---|---|---|
| 🔐 Security | 3/10 | 🔴 Critical gaps |
| ⚡ Performance | 4/10 | 🔴 No caching, no pagination |
| 🛡️ Reliability | 3/10 | 🔴 No resilience patterns |
| 📊 Observability | 5/10 | 🟡 Prometheus added, needs structured logging |
| 🧪 Testing | 2/10 | 🔴 18 tests for 211 source files |
| 💾 Data Integrity | 5/10 | 🟡 Indexes exist, no transactions |
| ⚙️ Configuration | 4/10 | 🔴 No prod profile, hardcoded secrets |
| 🚀 Deployment | 6/10 | 🟡 Dockerfile good, CI/CD minimal |
| 🖥️ Frontend | 6/10 | 🟡 Functional, needs security headers |
| 🔧 Operations | 3/10 | 🔴 No backup strategy, no runbooks |

---

## 🔴 Critical Issues (Must Fix)

### 1. No Production Profile
**Severity:** 🔴 CRITICAL

There is no `application-prod.yml`. The base [application.yml](file:///d:/git/promptly/apps/backend/core/src/main/resources/application.yml) defaults to `dev` profile:
```yaml
spring:
  profiles:
    active: dev  # ← hardcoded to dev!
```

**Impact:** If deployed without explicitly setting `SPRING_PROFILES_ACTIVE=prod`, the app runs in dev mode — potentially with permissive security, debug logging, and dev credentials.

**Fix:** Create `application-prod.yml` with:
- `spring.profiles.active: prod`
- Override JWT secret via env var (never hardcode)
- Set logging to `WARN` for framework, `INFO` for app
- Disable Swagger UI
- Restrict actuator endpoints

---

### 2. Hardcoded JWT Secret
**Severity:** 🔴 CRITICAL

[application.yml:L35](file:///d:/git/promptly/apps/backend/core/src/main/resources/application.yml#L35):
```yaml
jwt:
  secret: promptly-dev-secret-key-change-in-production-min-32-chars
```

This secret is **committed to Git**. Anyone with repo access can forge JWT tokens.

**Fix:** Use `${JWT_SECRET}` env var with no default. Fail fast on startup if not set.

---

### 3. No Rate Limiting
**Severity:** 🔴 CRITICAL

Zero rate limiting anywhere. The login endpoint (`POST /api/v1/auth/login`) is wide open for brute-force attacks.

**Fix:** Add `spring-boot-starter-rate-limiter` or use Resilience4j `@RateLimiter`:
- Login: 5 attempts / minute / IP
- API: 100 req/s / user
- Registration: 3 / hour / IP

---

### 4. No Security Headers
**Severity:** 🔴 CRITICAL

No `Content-Security-Policy`, `X-Frame-Options`, `X-Content-Type-Options`, `Strict-Transport-Security`, or `Referrer-Policy` headers on any response.

**Fix:** Add a `SecurityHeadersFilter` or configure via nginx:
```
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
Strict-Transport-Security: max-age=31536000; includeSubDomains
Content-Security-Policy: default-src 'self'
Referrer-Policy: strict-origin-when-cross-origin
```

---

### 5. CORS Not Configured for Production
**Severity:** 🔴 CRITICAL

No CORS filter exists in the backend. The SSE config only allows `localhost:4200` and `localhost:8080`:

[application.yml:L76-78](file:///d:/git/promptly/apps/backend/core/src/main/resources/application.yml#L76):
```yaml
allowed-origins:
  - "http://localhost:4200"
  - "http://localhost:8080"
```

**Fix:** Set production origins via env var: `${CORS_ALLOWED_ORIGINS}`. Add a global `CorsWebFilter` bean.

---

### 6. docker-compose.prod.yml Uses Wrong MongoDB URI Key
**Severity:** 🔴 CRITICAL

[docker-compose.prod.yml:L42](file:///d:/git/promptly/docker-compose.prod.yml#L42):
```yaml
- SPRING_DATA_MONGODB_URI=mongodb://mongodb:27017/promptly
```

But the base config uses `spring.mongodb.uri` (not `spring.data.mongodb.uri`). This was the exact bug that caused the perf test failures.

**Fix:** Change to `SPRING_MONGODB_URI`.

---

## 🟠 High-Severity Issues

### 7. No Caching Layer
**Severity:** 🟠 HIGH

Zero `@Cacheable`, no Caffeine/Redis configuration. Every request hits MongoDB directly. For read-heavy endpoints like `GET /prompts` and `GET /projects`, this is a significant performance bottleneck.

**Fix:** Add `spring-boot-starter-cache` + Caffeine for local caching, or Redis for distributed.

---

### 8. No Pagination
**Severity:** 🟠 HIGH

No `Pageable`, `PageRequest`, or `page/size` parameters anywhere. List endpoints return **all records**. This will cause OOM and timeouts as data grows.

**Fix:** Add `page`, `size`, `sort` query params to all list endpoints. Default page size 20, max 100.

---

### 9. No Resilience Patterns (Circuit Breakers, Retries)
**Severity:** 🟠 HIGH

No Resilience4j, no `@Retry`, no `@CircuitBreaker`. If MongoDB or the LLM provider goes down, every request will hang until timeout, cascading failures across all endpoints.

**Fix:** Add `resilience4j-spring-boot3`:
- Circuit breaker on LLM calls (Gemini API)
- Retry with backoff on MongoDB transient errors
- Bulkhead to limit concurrent LLM requests

---

### 10. Test Coverage: 8.5% (18/211 files)
**Severity:** 🟠 HIGH

Only 18 test files for 211 source files (~8.5% file coverage). No integration tests visible. Functional E2E tests exist but unit/integration coverage is critically low.

**Fix:** Target 70%+ coverage. Priority areas:
- Auth service (login, JWT generation/validation)
- Prompt CRUD operations
- Workflow state machine transitions
- Exception handler mappings

---

### 11. No Input Validation Annotations
**Severity:** 🟠 HIGH

No `@Valid`, `@NotNull`, `@NotBlank`, or `@Size` annotations found in the codebase. The `spring-boot-starter-validation` dependency is included but unused.

**Fix:** Add Bean Validation to all request DTOs and controller parameters.

---

### 12. Actuator Exposed Without Auth in Production
**Severity:** 🟠 HIGH

[LocalAuthSecurityConfig.java:L34](file:///d:/git/promptly/apps/backend/core/src/main/java/com/promptly/auth/infrastructure/security/LocalAuthSecurityConfig.java#L34):
```java
.pathMatchers("/actuator/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
```

All actuator endpoints (including `/env`, `/metrics`, `/prometheus`) are publicly accessible. This leaks JVM internals, environment variables, and system metrics.

**Fix:** In production:
- Only permit `/actuator/health` and `/actuator/prometheus` unauthenticated
- Require auth or IP-restrict all other actuator endpoints
- Disable Swagger entirely in prod

---

### 13. No Structured Logging
**Severity:** 🟠 HIGH

Logging is present (`@Slf4j` used in ~30 classes) but uses plain text format. No JSON logging, no correlation IDs, no request tracing.

**Fix:** Add `logback-spring.xml` with JSON layout for prod. Add `spring-cloud-starter-sleuth` or Micrometer Tracing for distributed trace IDs.

---

### 14. No Database Backup Strategy
**Severity:** 🟠 HIGH

MongoDB has no backup configuration — no `mongodump` schedule, no replica set, no point-in-time recovery.

**Fix:** At minimum:
- MongoDB replica set for HA
- Scheduled `mongodump` to S3/GCS
- Enable oplog for point-in-time recovery

---

### 15. SSE Endpoint Permits All Without Auth
**Severity:** 🟠 HIGH

[LocalAuthSecurityConfig.java:L32](file:///d:/git/promptly/apps/backend/core/src/main/java/com/promptly/auth/infrastructure/security/LocalAuthSecurityConfig.java#L32):
```java
.pathMatchers(HttpMethod.GET, "/api/v1/sse/**").permitAll()
```

SSE is auth-less. Any user can subscribe to any topic and receive real-time notifications meant for other users/projects.

**Fix:** Validate the token query parameter in the SSE handler and verify topic access authorization.

---

## 🟡 Medium-Severity Issues

### 16. No Graceful Shutdown Configuration
Spring Boot's graceful shutdown isn't configured. During deployments, in-flight requests (especially SSE streams) will be abruptly terminated.

**Fix:** Add `server.shutdown: graceful` and `spring.lifecycle.timeout-per-shutdown-phase: 30s`.

### 17. No Connection Pool Tuning
MongoDB connection pool uses defaults (max 100). Under load, this may be insufficient or wasteful.

**Fix:** Configure `spring.data.mongodb.connection-pool.max-size`, `min-size`, `max-wait-time`.

### 18. Auto-Index Creation Disabled But No Migration Strategy
[application.yml:L11](file:///d:/git/promptly/apps/backend/core/src/main/resources/application.yml#L11): `auto-index-creation: false` — correct for prod. But the `seed-data/init.js` index script is only run during perf tests. No migration tool (like Mongock) ensures indexes exist in production.

### 19. CI/CD Has Only E2E Workflow
Only [e2e.yml](file:///d:/git/promptly/.github/workflows/e2e.yml) exists. No build/test pipeline, no deployment workflow, no security scanning (SAST/DAST/dependency audit).

### 20. Dockerfile Uses `wget` for Healthcheck
Alpine image may not have `wget`. Use `curl` or a Spring Boot actuator probe instead.

---

## ✅ What's Done Well

| Area | Status |
|---|---|
| **Architecture** | ✅ Clean Spring Modulith with proper module boundaries |
| **Error Handling** | ✅ Comprehensive RFC 9457 `ProblemDetails` via `GlobalExceptionHandler` (12 exception types covered) |
| **OpenAPI Contract** | ✅ Contract-first with generated server interfaces and multi-platform SDKs |
| **Dockerfile** | ✅ Multi-stage, layered JAR, non-root user, JVM container tuning |
| **Event-Driven** | ✅ Spring Modulith events with SSE notification fan-out |
| **Healthchecks** | ✅ Docker and K8s readiness/liveness probes configured |
| **Observability** | ✅ Prometheus metrics with histograms and SLO buckets (just added) |
| **Perf Testing** | ✅ k6 load testing with automated orchestration |

---

## Recommended Priority Order

```
Week 1:  #2 (JWT secret) → #1 (prod profile) → #6 (compose fix) → #4 (security headers)
Week 2:  #3 (rate limiting) → #5 (CORS) → #12 (actuator auth) → #15 (SSE auth)
Week 3:  #11 (validation) → #8 (pagination) → #7 (caching)
Week 4:  #9 (resilience) → #13 (structured logging) → #10 (tests)
Ongoing: #14 (backups) → #19 (CI/CD) → #16-18 (operational)
```
