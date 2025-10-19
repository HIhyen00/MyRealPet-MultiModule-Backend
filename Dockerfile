# Stage 1: Build
FROM gradle:8.7-jdk17-alpine AS builder
WORKDIR /app

ARG SERVICE_NAME
COPY . .

# Build only the specific service
RUN if [ -d "./${SERVICE_NAME}" ]; then \
      cd ${SERVICE_NAME} && gradle clean build -x test; \
    else \
      echo "Error: ${SERVICE_NAME} not found"; exit 1; \
    fi

# Stage 2: Runtime
FROM openjdk:17-alpine
WORKDIR /app

ARG SERVICE_NAME
ENV SERVICE_NAME=${SERVICE_NAME}

# Copy JAR files (api / adminApi 모두 대응)
RUN mkdir -p /app
RUN if [ -d "/app/${SERVICE_NAME}/api/build/libs" ]; then \
      cp /app/${SERVICE_NAME}/api/build/libs/*.jar /app/app.jar; \
    elif [ -d "/app/${SERVICE_NAME}/adminApi/build/libs" ]; then \
      cp /app/${SERVICE_NAME}/adminApi/build/libs/*.jar /app/app.jar; \
    else \
      echo "No JAR file found for ${SERVICE_NAME}"; exit 1; \
    fi

COPY --from=builder /app/${SERVICE_NAME}/api/build/libs/*.jar ./ 2>/dev/null || true
COPY --from=builder /app/${SERVICE_NAME}/adminApi/build/libs/*.jar ./ 2>/dev/null || true

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -jar $(ls *.jar | head -n 1)"]
