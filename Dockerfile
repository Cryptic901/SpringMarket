ARG SERVICE_NAME
ARG SERVICE_PORT
ARG SERVICE_VERSION
ARG SERVICE_CONTEXT

FROM eclipse-temurin:21-jdk-alpine

ARG SERVICE_NAME
LABEL org.opencontainers.image.source="https://github.com/Cryptic901/SpringMarket"
LABEL org.opencontainers.image.description="${SERVICE_NAME} - Spring Market"
LABEL org.opencontainers.image.version="1.0.0"

RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

ADD --chown=spring:spring \
 https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar \
  /app/opentelemetry-javaagent.jar

ARG SERVICE_VERSION
ARG SERVICE_CONTEXT
COPY --chown=spring:spring ${SERVICE_CONTEXT}/target/${SERVICE_NAME}-${SERVICE_VERSION}.jar /app/app.jar

USER spring:spring

ENV JAVA_OPTS="-XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:+UseG1GC \
    -XX:+UseStringDeduplication \
    -Djava.security.egd=file:/dev/./urandom"

ARG SERVICE_PORT
EXPOSE ${SERVICE_PORT}

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]