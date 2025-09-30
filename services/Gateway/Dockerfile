FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/Gateway-1.0-SNAPSHOT.jar Gateway.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "Gateway.jar"]