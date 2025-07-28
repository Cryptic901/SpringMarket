FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/NotificationService-1.0-SNAPSHOT.jar NotificationService.jar
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "NotificationService.jar"]