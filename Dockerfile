# Multi-stage build for MyRealPet MultiModule Backend
# Supports: account, pet-walk, sns, pet-life-cycle, pet-life-cycle-admin, qna

ARG SERVICE_NAME

# ==========================================
# Build Stage
# ==========================================
FROM gradle:8.5-jdk17 AS builder

ARG SERVICE_NAME
WORKDIR /app

# Copy gradle wrapper and configuration files
COPY gradle gradle
COPY gradlew .
COPY gradlew.bat .
COPY build.gradle .
COPY settings.gradle .

# Copy all module sources
COPY common common
COPY account account
COPY pet-walk pet-walk
COPY sns sns
COPY pet-life-cycle pet-life-cycle
COPY qna qna

# Build the specific service based on SERVICE_NAME
RUN chmod +x ./gradlew && \
    echo "Building service: ${SERVICE_NAME}" && \
    if [ "${SERVICE_NAME}" = "pet-life-cycle-admin" ]; then \
        echo "Building pet-life-cycle:adminApi module..."; \
        ./gradlew :pet-life-cycle:adminApi:bootJar --no-daemon --stacktrace && \
        mkdir -p /app/output && \
        cp /app/pet-life-cycle/adminApi/build/libs/*.jar /app/output/app.jar; \
    else \
        echo "Building ${SERVICE_NAME}:api module..."; \
        ./gradlew :${SERVICE_NAME}:api:bootJar --no-daemon --stacktrace && \
        mkdir -p /app/output && \
        cp /app/${SERVICE_NAME}/api/build/libs/*.jar /app/output/app.jar; \
    fi && \
    echo "Build completed. JAR location:" && \
    ls -lah /app/output/

# ==========================================
# Runtime Stage
# ==========================================
FROM amazoncorretto:17-alpine

ARG SERVICE_NAME
WORKDIR /app

# Copy the built jar from unified output location
COPY --from=builder /app/output/app.jar app.jar

# Verify jar exists
RUN ls -lah /app/app.jar

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT:-8080}/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod}", \
    "-jar", \
    "app.jar"]
