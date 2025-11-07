FROM grafana/tempo:latest AS tempo

LABEL org.opencontainers.image.source="https://github.com/Cryptic901/SpringMarket"
LABEL org.opencontainers.image.description="Grafana Tempo - Spring Market"
LABEL org.opencontainers.image.version="1.0.0"

FROM busybox:1.36-glibc

COPY --from=tempo /tempo /tempo

COPY tempo.yaml /etc/tempo.yaml

RUN chmod +x /tempo

EXPOSE 3200 4317 4318

ENTRYPOINT ["/tempo"]
CMD ["-config.file=/etc/tempo.yaml"]