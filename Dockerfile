# ===========================================================================
# Promptly — Unified Production Dockerfile
# ===========================================================================
# Assembles a single container from PRE-BUILT artifacts:
#   - Backend:  apps/backend/core/target/*.jar  (Spring Boot JAR)
#   - Frontend: dist/promptly/browser/          (Angular production build)
#
# Prerequisites — build BOTH before running docker build:
#   mvn package -pl apps/backend/core -am -DskipTests -B
#   npx nx build promptly --configuration=production
#
# Build:  docker build -t spectrayan/promptly .
# Run:    docker run -p 3000:3000 -e MONGODB_URI=... spectrayan/promptly
#
# Exposed port: 3000 (Nginx serves frontend + reverse-proxies /api → backend:8080)
# ===========================================================================

# -- Stage 1: Extract layered Spring Boot JAR --------------------------------
FROM eclipse-temurin:21-jdk-alpine AS extract

WORKDIR /app

COPY apps/backend/core/target/*.jar app.jar

RUN java -Djarmode=layertools -jar app.jar extract --destination /app/layers

# -- Stage 2: Runtime (JRE + Nginx + Supervisor) -----------------------------
FROM eclipse-temurin:21-jre-alpine AS runtime

# Install nginx, supervisor, and curl (healthcheck)
RUN apk add --no-cache nginx supervisor curl

# Security: non-root user for the backend process
RUN addgroup -S promptly && adduser -S promptly -G promptly

# ── Backend (Spring Boot) ──────────────────────────────────────────
WORKDIR /app

COPY --from=extract /app/layers/dependencies/ ./
COPY --from=extract /app/layers/spring-boot-loader/ ./
COPY --from=extract /app/layers/snapshot-dependencies/ ./
COPY --from=extract /app/layers/application/ ./

RUN chown -R promptly:promptly /app

# ── Frontend (pre-built Angular assets) ────────────────────────────
RUN rm -f /etc/nginx/http.d/default.conf

COPY dist/promptly/browser /usr/share/nginx/html
COPY infra/docker/nginx.conf /etc/nginx/http.d/promptly.conf

RUN chown -R nginx:nginx /usr/share/nginx/html && \
    chmod -R 755 /usr/share/nginx/html

# ── Supervisor config ─────────────────────────────────────────────
COPY infra/docker/supervisord.conf /etc/supervisord.conf

# ── JVM tuning ────────────────────────────────────────────────────
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:InitialRAMPercentage=50.0 \
               -Djava.security.egd=file:/dev/./urandom"

# Nginx pid directory
RUN mkdir -p /run/nginx && chown nginx:nginx /run/nginx

EXPOSE 3000

HEALTHCHECK --interval=30s --timeout=10s --retries=3 --start-period=60s \
  CMD curl -sf http://localhost:3000/health && curl -sf http://localhost:8080/actuator/health || exit 1

CMD ["supervisord", "-c", "/etc/supervisord.conf"]
