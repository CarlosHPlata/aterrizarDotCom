# syntax=docker/dockerfile:1.7
# Build context must be ./docker so the wiremock/ stubs are reachable:
#   docker build -f docker/external/Dockerfile -t aterrizar-external-mocks docker/

FROM wiremock/wiremock:latest

COPY --chown=wiremock:wiremock wiremock/mappings /home/wiremock/mappings
COPY --chown=wiremock:wiremock wiremock/__files  /home/wiremock/__files

EXPOSE 8080

ENTRYPOINT ["/docker-entrypoint.sh", "--global-response-templating", "--disable-gzip", "--verbose"]

ARG BUILD_VERSION=0.0.1-SNAPSHOT
ARG BUILD_REVISION=unknown
LABEL org.opencontainers.image.title="aterrizar-external-mocks" \
      org.opencontainers.image.description="Standalone WireMock image bundling all aterrizar external-service stubs" \
      org.opencontainers.image.source="https://github.com/aterrizar/aterrizarDotCom" \
      org.opencontainers.image.version="${BUILD_VERSION}" \
      org.opencontainers.image.revision="${BUILD_REVISION}" \
      org.opencontainers.image.vendor="Aterrizar"
