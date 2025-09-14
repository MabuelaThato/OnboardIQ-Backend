# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build

LABEL maintainers="Thato Mabuela <mabuelathato03@gmail.com>"

RUN apt-get update && \
    apt-get install -y git

WORKDIR /app/
COPY . /app/

RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build target/ticketsystem-1.0.0-jar-with-dependencies.jar /app/ticketsystem-1.0.0.jar

EXPOSE 7000

ENTRYPOINT ["java", "-jar", "/app/ticketsystem-1.0.0.jar"]
CMD []