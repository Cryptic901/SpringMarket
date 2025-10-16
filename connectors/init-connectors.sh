#!/bin/bash

DEBEZIUM_URL="http://localhost:3434"
CONNECTORS_DIR="../connectors"

echo "Waiting for Debezium Connect to be ready..."
until curl -s $DEBEZIUM_URL/ | grep -q "version"; do
  echo "Debezium not ready yet, waiting..."
  sleep 5
done

echo "Debezium Connect is ready!"

# Регистрируем все коннекторы
for connector_file in "$CONNECTORS_DIR"/*.json; do
  if [ -f "$connector_file" ]; then
    connector_name=$(basename "$connector_file" .json)
    echo "Registering connector: $connector_name"

    # Проверяем, существует ли уже
    if curl -s $DEBEZIUM_URL/connectors/"$connector_name" | grep -q "error_code"; then
      echo "Connector $connector_name does not exist, creating..."
      curl -X POST $DEBEZIUM_URL/connectors \
        -H "Content-Type: application/json" \
        -d @"$connector_file"
      echo ""
    else
      echo "Connector $connector_name already exists, skipping..."
    fi
  fi
done

echo "All connectors registered!"