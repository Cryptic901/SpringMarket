FROM eclipse-temurin:21-jdk-alpine

LABEL org.opencontainers.image.source="https://github.com/Cryptic901/SpringMarket"
LABEL org.opencontainers.image.description="Order Service - Spring Market"
LABEL org.opencontainers.image.version="1.0.0"

RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

ADD --chown=spring:spring \
 https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar \
  /app/opentelemetry-javaagent.jar

COPY --chown=spring:spring target/order-service-1.0-SNAPSHOT.jar /app/order-service.jar

USER spring:spring

ENV JAVA_OPTS="-XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:+UseG1GC \
    -XX:+UseStringDeduplication \
    -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8082

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar order-service.jar"]