FROM navikt/java:17
COPY ./build/libs/permitteringsmelding-notifikasjon-all.jar app.jar

EXPOSE 8080
