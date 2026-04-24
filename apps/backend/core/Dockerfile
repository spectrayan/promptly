# ═══════════════════════════════════════════════════════════════════════
# Promptly Backend — Multi-stage Production Dockerfile
# ═══════════════════════════════════════════════════════════════════════
# Produces a lean JRE-only image with layered JAR for optimal caching.
#
# Build:  docker build -t promptly-backend -f apps/backend/core/Dockerfile .
# Run:    docker run -p 8080:8080 -e SPRING_DATA_MONGODB_URI=... promptly-backend

# ── Stage 1: Build ──────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /workspace

# Copy POM files first (for dependency caching)
COPY pom.xml ./
COPY libs/shared/openapi-spec/pom.xml libs/shared/openapi-spec/
COPY libs/shared/apis/v1/promptly-apis/pom.xml libs/shared/apis/v1/promptly-apis/
COPY apps/backend/core/pom.xml apps/backend/core/

# Download dependencies (cached layer)
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -B -pl apps/backend/core -am

# Copy source code
COPY libs/ libs/
COPY apps/backend/ apps/backend/

# Build the application
RUN --mount=type=cache,target=/root/.m2 \
    mvn package -B -pl apps/backend/core -am -DskipTests \
    && mkdir -p /app/layers \
    && java -Djarmode=layertools -jar apps/backend/core/target/*.jar extract --destination /app/layers

# ── Stage 2: Runtime ────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime

# Security: non-root user
RUN addgroup -S promptly && adduser -S promptly -G promptly

WORKDIR /app

# Copy layered JAR (optimized Docker cache)
COPY --from=build /app/layers/dependencies/ ./
COPY --from=build /app/layers/spring-boot-loader/ ./
COPY --from=build /app/layers/snapshot-dependencies/ ./
COPY --from=build /app/layers/application/ ./

# Set ownership
RUN chown -R promptly:promptly /app

USER promptly

# JVM tuning for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:InitialRAMPercentage=50.0 \
               -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
