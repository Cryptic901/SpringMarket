ARG SERVICE_NAME
ARG SERVICE_VERSION
ARG SERVICE_CONTEXT
ARG SERVICE_PORT

# ============================================
# Stage 1: Build stage
# ============================================
FROM eclipse-temurin:21-jdk-alpine AS builder

ARG SERVICE_NAME
ARG SERVICE_VERSION
ARG SERVICE_CONTEXT

WORKDIR /build

# Копируем Maven wrapper и родительский pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Копируем pom.xml конкретного сервиса для кэширования зависимостей
COPY ${SERVICE_CONTEXT}/pom.xml ${SERVICE_CONTEXT}/

# Загружаем зависимости (этот слой будет кэшироваться если pom.xml не менялся)
RUN ./mvnw dependency:go-offline -pl ${SERVICE_CONTEXT} -am -B || true

# Копируем исходный код конкретного сервиса
COPY ${SERVICE_CONTEXT}/src ${SERVICE_CONTEXT}/src

# Собираем конкретный сервис
RUN ./mvnw clean package -pl ${SERVICE_CONTEXT} -am -DskipTests -B

# Извлекаем слои Spring Boot для оптимального кэширования Docker
WORKDIR /build/${SERVICE_CONTEXT}/target
RUN java -Djarmode=layertools -jar ${SERVICE_NAME}-${SERVICE_VERSION}.jar extract

# ============================================
# Stage 2: Runtime stage (JRE - экономия ~200MB)
# ============================================
FROM eclipse-temurin:21-jre-alpine

ARG SERVICE_NAME
ARG SERVICE_PORT
ARG SERVICE_VERSION

LABEL org.opencontainers.image.source="https://github.com/Cryptic901/SpringMarket"
LABEL org.opencontainers.image.description="${SERVICE_NAME} - Spring Market"
LABEL org.opencontainers.image.version="${SERVICE_VERSION}"

# Создаём непривилегированного пользователя
RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

# Скачиваем OpenTelemetry agent
ADD --chown=spring:spring \
    https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar \
    /app/opentelemetry-javaagent.jar

# Копируем слои приложения из builder stage
# Порядок важен: от наименее изменяемых к наиболее изменяемым
ARG SERVICE_CONTEXT
COPY --from=builder --chown=spring:spring /build/${SERVICE_CONTEXT}/target/dependencies/ ./
COPY --from=builder --chown=spring:spring /build/${SERVICE_CONTEXT}/target/spring-boot-loader/ ./
COPY --from=builder --chown=spring:spring /build/${SERVICE_CONTEXT}/target/snapshot-dependencies/ ./
COPY --from=builder --chown=spring:spring /build/${SERVICE_CONTEXT}/target/application/ ./

USER spring:spring

# Оптимизированные JVM параметры для контейнера
ENV JAVA_OPTS="-XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:+UseG1GC \
    -XX:+UseStringDeduplication \
    -XX:MaxGCPauseMillis=200 \
    -Djava.security.egd=file:/dev/./urandom"

EXPOSE ${SERVICE_PORT}

# Используем Spring Boot Layered JAR для оптимального старта
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]