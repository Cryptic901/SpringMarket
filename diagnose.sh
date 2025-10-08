#!/bin/bash

echo "=== Checking Kafka Brokers ==="
docker exec kafka-1 opt/kafka/bin/kafka-broker-api-versions.sh --bootstrap-server kafka-1:9090 2>&1 | head -5

echo -e "\n=== Checking Debezium Connect ==="
curl -s http://localhost:3434/ | jq .

echo -e "\n=== Checking Connectors ==="
curl -s http://localhost:3434/connectors | jq .

echo -e "\n=== Checking Topics ==="
docker exec kafka-1 opt/kafka/bin/kafka-topics.sh --list --bootstrap-server kafka-1:9090

echo -e "\n=== Checking Consumer Groups ==="
docker exec kafka-1 opt/kafka/bin/kafka-consumer-groups.sh --bootstrap-server kafka-1:9090 --list

echo -e "\n=== Checking Outbox Tables ==="
docker exec postgres psql -U postgres -d product_db -c \
  "SELECT COUNT(*) as product_outbox_count FROM product_schema.outbox;"
docker exec postgres psql -U postgres -d category_db -c \
  "SELECT COUNT(*) as category_outbox_count FROM category_schema.outbox;"

echo -e "\n=== Checking Service Logs for Errors ==="
docker logs product-service 2>&1 | grep -i error | tail -5
docker logs category-service 2>&1 | grep -i error | tail -5

echo -e "\n=== Resource Usage ==="
docker stats --no-stream --format "table {{.Name}}\t{{.CPUPerc}}\t{{.MemUsage}}"