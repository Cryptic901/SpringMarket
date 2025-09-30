FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/PaymentService-1.0-SNAPSHOT.jar PaymentService.jar
EXPOSE 8088
ENTRYPOINT ["java", "-jar", "PaymentService.jar"]