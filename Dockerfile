# ------------------------
# Stage 1: Build
# ------------------------
FROM gradle:8.7-jdk17-alpine AS builder
WORKDIR /app

ARG SERVICE_NAME
COPY . .

# Build only the specified service
RUN chmod +x ./gradlew && \
    if [ -d "./${SERVICE_NAME}/api" ]; then \
        ./gradlew :${SERVICE_NAME}:api:bootJar --no-daemon; \
    elif [ -d "./${SERVICE_NAME}/adminApi" ]; then \
        ./gradlew :${SERVICE_NAME}:adminApi:bootJar --no-daemon; \
    else \
        echo "Error: Module ${SERVICE_NAME} not found"; exit 1; \
    fi

# ------------------------
# Stage 2: Runtime
# ------------------------
FROM amazoncorretto:17-alpine
WORKDIR /app

ARG SERVICE_NAME

# Copy the built JAR(s) from builder stage
COPY --from=builder /app/${SERVICE_NAME}/api/build/libs/*.jar ./ 2>/dev/null || true
COPY --from=builder /app/${SERVICE_NAME}/adminApi/build/libs/*.jar ./ 2>/dev/null || true

# Expose default port
EXPOSE 8080

# Run the first JAR found in the folder
ENTRYPOINT ["sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod} -jar $(ls *.jar | head -n 1)"]
