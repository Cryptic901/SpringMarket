FROM grafana/tempo:latest AS tempo

FROM busybox:1.36-glibc

COPY --from=tempo /tempo /tempo

COPY tempo.yaml /etc/tempo.yaml

RUN chmod +x /tempo

EXPOSE 3200 4317 4318

ENTRYPOINT ["/tempo"]
CMD ["-config.file=/etc/tempo.yaml"]