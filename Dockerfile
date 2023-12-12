FROM ghcr.io/navikt/baseimages/temurin:17

COPY build/permitteringsmelding-notifikasjon.jar app.jar
COPY build/libs libs

USER apprunner