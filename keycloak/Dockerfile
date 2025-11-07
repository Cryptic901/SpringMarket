FROM keycloak/keycloak:26.0.0 AS builder

LABEL org.opencontainers.image.source="https://github.com/Cryptic901/SpringMarket"
LABEL org.opencontainers.image.description="Kafka-Keycloak SPI - Spring Market"
LABEL org.opencontainers.image.version="1.0.0"

COPY /providers/kafka-keycloak-spi.jar /opt/keycloak/providers/

RUN /opt/keycloak/bin/kc.sh build

FROM keycloak/keycloak:26.0.0

COPY --from=builder /opt/keycloak/ /opt/keycloak/

ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]