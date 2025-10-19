# Multi-stage build for MyRealPet MultiModule Backend
# This Dockerfile can build all microservices: account, pet-walk, sns, pet-life-cycle, qna, and pet-life-cycle-admin

ARG SERVICE_NAME

# Build stage
FROM gradle:8.5-jdk17 AS builder

ARG SERVICE_NAME
WORKDIR /app

# Copy all project files
COPY . .

# Grant execute permission and build the specific service
# Handles both regular api and adminApi modules
RUN chmod +x ./gradlew && \
    if echo "${SERVICE_NAME}" | grep -q "/"; then \
        MODULE_PATH=$(echo ${SERVICE_NAME} | sed 's/\//:/g'); \
        echo "Building module with path: ${MODULE_PATH}"; \
        ./gradlew :${MODULE_PATH}:bootJar --no-daemon; \
    else \
        echo "Building standard api module: ${SERVICE_NAME}:api"; \
        ./gradlew :${SERVICE_NAME}:api:bootJar --no-daemon; \
    fi && \
    echo "Removing plain jars..." && \
    find /app -name "*-plain.jar" -type f -delete && \
    echo "Remaining jars:" && \
    find /app -name "*.jar" -type f

# Runtime stage
FROM amazoncorretto:17-alpine

ARG SERVICE_NAME
WORKDIR /app

# Copy the built jar from builder stage
# Handles both regular api and adminApi modules
# Only bootJar files remain (plain jars were deleted in build stage)
COPY --from=builder /app/${SERVICE_NAME}/**/build/libs/*.jar app.jar

# Add healthcheck (port will be configured in docker-compose)
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT:-8080}/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod}", \
    "-jar", \
    "app.jar"]
