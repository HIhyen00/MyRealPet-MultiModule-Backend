# Multi-stage build for MyRealPet MultiModule Backend
# This Dockerfile can build both account and pet-walk services

ARG SERVICE_NAME

# Build stage
FROM gradle:8.5-jdk17 AS builder

ARG SERVICE_NAME
WORKDIR /app

# Copy all project files
COPY . .

# Grant execute permission and build the specific service
RUN chmod +x ./gradlew && \
    ./gradlew :${SERVICE_NAME}:api:bootJar --no-daemon

# Runtime stage
FROM amazoncorretto:17-alpine

ARG SERVICE_NAME
WORKDIR /app

# Copy the built jar from builder stage
COPY --from=builder /app/${SERVICE_NAME}/api/build/libs/*.jar app.jar

# Add healthcheck (port will be configured in docker-compose)
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT:-8080}/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod}", \
    "-jar", \
    "app.jar"]
