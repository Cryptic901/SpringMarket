FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/CategoryService-1.0-SNAPSHOT.jar CategoryService.jar
EXPOSE 8091
ENTRYPOINT ["java", "-jar", "CategoryService.jar"]