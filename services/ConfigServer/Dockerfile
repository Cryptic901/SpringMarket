FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/ConfigServer-1.0-SNAPSHOT.jar ConfigServer.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "ConfigServer.jar"]