# Runtime stage
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/ticketsystem-${VERSION}.jar /app/ticketsystem-${VERSION}.jar

EXPOSE 7000

ENTRYPOINT ["java", "-jar", "/app/ticketsystem-${VERSION}.jar"]
CMD []