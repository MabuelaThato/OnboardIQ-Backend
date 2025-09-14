# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build

LABEL maintainers="Thato Mabuela <mabuelathato03@gmail.com>"

RUN apt-get update && \
    apt-get install -y git

WORKDIR /app/
COPY . /app/

ARG VERSION
ENV VERSION=1.0.0

RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build target/ticketsystem-${VERSION}-jar-with-dependencies.jar /app/ticketsystem-${VERSION}.jar

EXPOSE 7000

ENTRYPOINT ["java", "-jar", "/app/ticketsystem-${VERSION}.jar"]
CMD []