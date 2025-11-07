FROM keycloak/keycloak:26.0.0 AS builder

COPY kafka-keycloak-spi-1.0-SNAPSHOT.jar /opt/keycloak/providers/

RUN /opt/keycloak/bin/kc.sh build

FROM keycloak/keycloak:26.0.0

COPY --from=builder /opt/keycloak/ /opt/keycloak/

ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]