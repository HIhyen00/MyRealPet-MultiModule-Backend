# Multi-stage build for MyRealPet MultiModule Backend
ARG SERVICE_NAME

# Build stage
FROM gradle:8.5-jdk17 AS builder
ARG SERVICE_NAME
WORKDIR /app

COPY . .

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
    echo "Copying built jar to /app/app.jar..." && \
    find /app -name "*-SNAPSHOT.jar" -exec cp {} /app/app.jar \;

# Runtime stage
FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=builder /app/app.jar app.jar

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT:-8080}/actuator/health || exit 1

ENTRYPOINT ["java", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod}", \
  "-jar", "app.jar"]
