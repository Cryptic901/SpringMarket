FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/OrderService-1.0-SNAPSHOT.jar OrderService.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "OrderService.jar"]