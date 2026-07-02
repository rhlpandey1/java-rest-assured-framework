# syntax=docker/dockerfile:1.7
# ---------- Build & test image for the REST Assured framework ----------
FROM eclipse-temurin:21-jdk-alpine AS base

ENV GRADLE_USER_HOME=/home/gradle/.gradle \
    LANG=C.UTF-8 \
    LC_ALL=C.UTF-8

RUN apk add --no-cache bash git curl

WORKDIR /workspace

# Cache the wrapper + manifests first for better layer reuse
COPY gradlew settings.gradle build.gradle /workspace/
COPY gradle /workspace/gradle
RUN chmod +x gradlew && ./gradlew --version

# Now copy the sources
COPY src /workspace/src

# Default env can be overridden at runtime: -e ENV=staging
ENV ENV=dev

ENTRYPOINT ["./gradlew"]
CMD ["clean", "test", "-Denv=${ENV}", "--no-daemon"]
