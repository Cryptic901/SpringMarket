FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/CartService-1.0-SNAPSHOT.jar CartService.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "CartService.jar"]