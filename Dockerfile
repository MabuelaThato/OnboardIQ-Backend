FROM openjdk:17-jdk-slim
COPY target/ticketsystem-1.0-SNAPSHOT-jar-with-dependencies.jar /app.jar
EXPOSE 7000
ENTRYPOINT ["java", "-jar", "/app.jar"]