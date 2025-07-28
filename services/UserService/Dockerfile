FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/UserService-1.0-SNAPSHOT.jar UserService.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "UserService.jar"]