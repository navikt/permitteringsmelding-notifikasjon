FROM gcr.io/distroless/java21-debian12
COPY build/permitteringsmelding-notifikasjon-1.0.0.jar app.jar
COPY build/libs libs
CMD ["app.jar"]
