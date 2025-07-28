FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/ReviewService-1.0-SNAPSHOT.jar ReviewService.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "ReviewService.jar"]