FROM ghcr.io/navikt/baseimages/temurin:17

COPY build/permitteringsmelding-notifikasjon-1.0.0.jar app.jar
COPY build/libs libs

USER apprunner