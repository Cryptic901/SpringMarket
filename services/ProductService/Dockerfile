FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/ProductService-1.0-SNAPSHOT.jar ProductService.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "ProductService.jar"]