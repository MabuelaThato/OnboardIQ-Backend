# Runtime stage
FROM eclipse-temurin:21-jre

EXPOSE 7000

WORKDIR /app/
COPY . /app/

ARG VERSION
ENV VERSION=${VERSION}

COPY libs/ticketsystem-${VERSION}.jar  /app/ticketsystem-${VERSION}.jar

ENTRYPOINT ["java", "-jar", "/app/ticketsystem-${VERSION}.jar"]
CMD []
