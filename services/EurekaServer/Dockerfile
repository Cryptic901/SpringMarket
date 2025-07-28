FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/EurekaServer-1.0-SNAPSHOT.jar EurekaServer.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "EurekaServer.jar"]