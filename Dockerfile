# =====================================
# Multi-stage build for MyRealPet Backend
# Supports modules like account, qna, sns, petwalk, petlifecycle, petlifecycle-admin
# =====================================

# ---- Build Stage ----
FROM gradle:8.5-jdk17 AS builder

ARG SERVICE_NAME
WORKDIR /app

# Copy entire project
COPY . .

# Build target module (api or adminApi)
RUN chmod +x ./gradlew && \
    if echo "${SERVICE_NAME}" | grep -q "/"; then \
        MODULE_PATH=$(echo ${SERVICE_NAME} | sed 's/\//:/g'); \
        echo "▶ Building submodule path: ${MODULE_PATH}"; \
        ./gradlew :${MODULE_PATH}:bootJar --no-daemon; \
    else \
        echo "▶ Building standard module: ${SERVICE_NAME}:api"; \
        ./gradlew :${SERVICE_NAME}:api:bootJar --no-daemon || \
        ./gradlew :${SERVICE_NAME}:adminApi:bootJar --no-daemon; \
    fi && \
    echo "🧹 Removing plain jars..." && \
    find /app -name "*-plain.jar" -type f -delete && \
    echo "✅ Remaining JARs:" && \
    find /app -name "*.jar" -type f

# ---- Runtime Stage ----
FROM amazoncorretto:17-alpine

ARG SERVICE_NAME
WORKDIR /app

# Copy built jar (handles both api and adminApi automatically)
COPY --from=builder /app/${SERVICE_NAME}/api/build/libs/*.jar ./ || true
COPY --from=builder /app/${SERVICE_NAME}/adminApi/build/libs/*.jar ./ || true

# Find the built jar and rename to app.jar
RUN JAR_FILE=$(find . -name "*.jar" | head -n 1) && \
    echo "▶ Using JAR: $JAR_FILE" && \
    mv "$JAR_FILE" app.jar

# Healthcheck (optional)
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT:-8080}/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod}", \
    "-jar", \
    "app.jar"]
