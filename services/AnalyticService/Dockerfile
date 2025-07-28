FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/AnalyticService-1.0-SNAPSHOT.jar AnalyticService.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "AnalyticService.jar"]