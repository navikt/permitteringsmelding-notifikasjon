FROM navikt/java:17
COPY ./build/libs/permitteringsvarsel-notifikasjon-all.jar app.jar

EXPOSE 8080
