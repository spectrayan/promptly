# ===========================================================================
# Promptly Backend -- Multi-stage Production Dockerfile
# ===========================================================================
# Produces a lean JRE-only image from a PRE-BUILT JAR.
#
# Prerequisites: Build the JAR locally BEFORE running docker build:
#   mvn package -f apps/backend/core/pom.xml -DskipTests -B
#
# Build:  docker build -t promptly-backend -f apps/backend/core/Dockerfile .
# Run:    docker run -p 8080:8080 -e SPRING_DATA_MONGODB_URI=... promptly-backend

# -- Stage 1: Extract layered JAR -------------------------------------------
FROM eclipse-temurin:21-jdk-alpine AS extract

WORKDIR /app

# Copy the pre-built JAR from local build output
COPY apps/backend/core/target/*.jar app.jar

# Extract Spring Boot layered JAR for optimal Docker caching
RUN java -Djarmode=layertools -jar app.jar extract --destination /app/layers

# -- Stage 2: Runtime --------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine AS runtime

# Security: non-root user
RUN addgroup -S promptly && adduser -S promptly -G promptly

WORKDIR /app

# Copy layered JAR (optimized Docker cache -- dependencies change less often)
COPY --from=extract /app/layers/dependencies/ ./
COPY --from=extract /app/layers/spring-boot-loader/ ./
COPY --from=extract /app/layers/snapshot-dependencies/ ./
COPY --from=extract /app/layers/application/ ./

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

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher $SPRING_ARGS"]
