# syntax=docker/dockerfile:1.7

# ---------- Build stage ----------
FROM eclipse-temurin:24-jdk AS build

WORKDIR /workspace

COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
COPY config ./config

COPY service/build.gradle ./service/
COPY http/build.gradle ./http/
COPY integration/build.gradle ./integration/

RUN chmod +x ./gradlew && ./gradlew --no-daemon help >/dev/null 2>&1 || true

COPY service ./service
COPY http ./http
COPY integration ./integration

RUN ./gradlew --no-daemon :http:bootJar -x test

RUN cp http/build/libs/*.jar /workspace/app.jar \
    && java -Djarmode=tools -jar /workspace/app.jar extract \
        --layers --launcher --destination /workspace/extracted

# ---------- Runtime stage ----------
FROM eclipse-temurin:24-jre AS runtime

ARG APP_UID=1001
ARG APP_GID=1001
RUN groupadd --system --gid ${APP_GID} app \
    && useradd --system --uid ${APP_UID} --gid app --home-dir /app --shell /sbin/nologin app

WORKDIR /app

COPY --from=build --chown=app:app /workspace/extracted/dependencies/ ./
COPY --from=build --chown=app:app /workspace/extracted/spring-boot-loader/ ./
COPY --from=build --chown=app:app /workspace/extracted/snapshot-dependencies/ ./
COPY --from=build --chown=app:app /workspace/extracted/application/ ./

USER app

EXPOSE 8080

ENV JAVA_OPTS="" \
    SERVER_PORT=8080 \
    SPRING_PROFILES_ACTIVE="prod"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]

ARG BUILD_VERSION=0.0.1-SNAPSHOT
ARG BUILD_REVISION=unknown
LABEL org.opencontainers.image.title="aterrizar-service" \
      org.opencontainers.image.source="https://github.com/aterrizar/aterrizarDotCom" \
      org.opencontainers.image.version="${BUILD_VERSION}" \
      org.opencontainers.image.revision="${BUILD_REVISION}" \
      org.opencontainers.image.vendor="Aterrizar"
