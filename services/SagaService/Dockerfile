FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/SagaService-1.0-SNAPSHOT.jar SagaService.jar
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "SagaService.jar"]