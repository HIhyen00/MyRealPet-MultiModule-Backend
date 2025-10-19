# Multi-stage build for MyRealPet MultiModule Backend
# Supports: account, pet-walk, sns, pet-life-cycle, pet-life-cycle-admin, qna

ARG SERVICE_NAME

# ==========================================
# Build Stage
# ==========================================
FROM gradle:8.5-jdk17 AS builder

ARG SERVICE_NAME
WORKDIR /app

# Copy gradle files first for caching
COPY gradle gradle
COPY gradlew .
COPY gradlew.bat .
COPY build.gradle .
COPY settings.gradle .

# Copy all source files
COPY common common
COPY account account
COPY pet-walk pet-walk
COPY sns sns
COPY pet-life-cycle pet-life-cycle
COPY qna qna

# Build the specific service
RUN chmod +x ./gradlew && \
    if [ "${SERVICE_NAME}" = "pet-life-cycle-admin" ]; then \
        echo "Building pet-life-cycle:adminApi..."; \
        ./gradlew :pet-life-cycle:adminApi:bootJar --no-daemon --stacktrace; \
    else \
        echo "Building ${SERVICE_NAME}:api..."; \
        ./gradlew :${SERVICE_NAME}:api:bootJar --no-daemon --stacktrace; \
    fi

# ==========================================
# Runtime Stage
# ==========================================
FROM amazoncorretto:17-alpine

ARG SERVICE_NAME
WORKDIR /app

# Copy the built jar
RUN if [ "${SERVICE_NAME}" = "pet-life-cycle-admin" ]; then \
        echo "Runtime for pet-life-cycle-admin"; \
    else \
        echo "Runtime for ${SERVICE_NAME}"; \
    fi

COPY --from=builder /app/${SERVICE_NAME}/api/build/libs/*.jar app.jar 2>/dev/null || \
     COPY --from=builder /app/pet-life-cycle/adminApi/build/libs/*.jar app.jar

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT:-8080}/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod}", \
    "-jar", \
    "app.jar"]