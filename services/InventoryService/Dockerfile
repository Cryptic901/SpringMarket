FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/InventoryService-1.0-SNAPSHOT.jar InventoryService.jar
EXPOSE 8087
ENTRYPOINT ["java", "-jar", "InventoryService.jar"]