#!/bin/bash

# Скрипт для диагностики Kafka и Debezium в Kubernetes

NAMESPACE="messaging"  # Замените на ваш namespace

echo "=== 1. ПРОВЕРКА ПОДОВ KAFKA И DEBEZIUM ==="
kubectl get pods -n $NAMESPACE | grep -E "kafka|debezium|connect"

echo -e "\n=== 2. ЛОГИ DEBEZIUM CONNECTOR ==="
DEBEZIUM_POD=$(kubectl get pods -n $NAMESPACE -l app=debezium -o jsonpath='{.items[0].metadata.name}')
if [ ! -z "$DEBEZIUM_POD" ]; then
    echo "Pod: $DEBEZIUM_POD"
    kubectl logs -n $NAMESPACE "$DEBEZIUM_POD" --tail=50
else
    echo "Debezium pod не найден, проверяем Kafka Connect..."
    CONNECT_POD=$(kubectl get pods -n $NAMESPACE -l app=kafka-connect -o jsonpath='{.items[0].metadata.name}')
    if [ ! -z "$CONNECT_POD" ]; then
        kubectl logs -n $NAMESPACE "$CONNECT_POD" --tail=50
    fi
fi

echo -e "\n=== 3. ПРОВЕРКА KAFKA БРОКЕРОВ ==="
KAFKA_POD=$(kubectl get pods -n $NAMESPACE -l app=kafka -o jsonpath='{.items[0].metadata.name}')
if [ ! -z "$KAFKA_POD" ]; then
    echo "Kafka Pod: $KAFKA_POD"

    # Список топиков
    echo -e "\n--- Список топиков ---"
    kubectl exec -n $NAMESPACE "$KAFKA_POD" -- kafka-topics.sh \
        --bootstrap-server localhost:9092 \
        --list

    # Информация о Debezium топиках
    echo -e "\n--- Debezium топики (должны начинаться с префикса сервера) ---"
    kubectl exec -n $NAMESPACE "$KAFKA_POD" -- kafka-topics.sh \
        --bootstrap-server localhost:9092 \
        --list | grep -E "dbserver|postgres|mysql|debezium"

    # Описание конкретного топика (замените на свой)
    TOPIC_NAME="dbserver1.public.your_table"  # ЗАМЕНИТЕ!
    echo -e "\n--- Описание топика: $TOPIC_NAME ---"
    kubectl exec -n $NAMESPACE "$KAFKA_POD" -- kafka-topics.sh \
        --bootstrap-server localhost:9092 \
        --describe \
        --topic $TOPIC_NAME 2>/dev/null || echo "Топик $TOPIC_NAME не найден"

    # Проверка сообщений в топике (последние 10)
    echo -e "\n--- Последние сообщения из топика ---"
    kubectl exec -n $NAMESPACE "$KAFKA_POD" -- kafka-console-consumer.sh \
        --bootstrap-server localhost:9092 \
        --topic $TOPIC_NAME \
        --from-beginning \
        --max-messages 10 \
        --timeout-ms 5000 2>/dev/null || echo "Не удалось прочитать сообщения"

    # Consumer groups
    echo -e "\n--- Consumer Groups ---"
    kubectl exec -n $NAMESPACE "$KAFKA_POD" -- kafka-consumer-groups.sh \
        --bootstrap-server localhost:9092 \
        --list

else
    echo "Kafka pod не найден!"
fi

echo -e "\n=== 4. ПРОВЕРКА KAFKA CONNECT (DEBEZIUM) ==="
CONNECT_POD=$(kubectl get pods -n $NAMESPACE -l app=kafka-connect -o jsonpath='{.items[0].metadata.name}')
if [ ! -z "$CONNECT_POD" ]; then
    echo "Connect Pod: $CONNECT_POD"

    # Порт-форвард для доступа к REST API
    echo -e "\n--- Запуск port-forward для Kafka Connect REST API ---"
    kubectl port-forward -n $NAMESPACE "$CONNECT_POD" 8083:8083 &
    PF_PID=$!
    sleep 3

    # Список коннекторов
    echo -e "\n--- Установленные коннекторы ---"
    curl -s http://localhost:8083/connectors | jq .

    # Статус коннекторов
    echo -e "\n--- Статус коннекторов ---"
    for connector in $(curl -s http://localhost:8083/connectors | jq -r '.[]'); do
        echo -e "\nКоннектор: $connector"
        curl -s http://localhost:8083/connectors/"$connector"/status | jq .
    done

    # Убить port-forward
    kill $PF_PID 2>/dev/null
fi

echo -e "\n=== 5. ПРОВЕРКА ПОДКЛЮЧЕНИЯ К POSTGRESQL ==="
POSTGRES_POD=$(kubectl get pods -n $NAMESPACE -l app=postgres -o jsonpath='{.items[0].metadata.name}')
if [ ! -z "$POSTGRES_POD" ]; then
    echo "PostgreSQL Pod: $POSTGRES_POD"

    # Проверка репликации
    echo -e "\n--- Слоты репликации ---"
    kubectl exec -n $NAMESPACE "$POSTGRES_POD" -- psql -U postgres -c "SELECT * FROM pg_replication_slots;"

    # Проверка публикаций
    echo -e "\n--- Публикации (publications) ---"
    kubectl exec -n $NAMESPACE "$POSTGRES_POD" -- psql -U postgres -c "SELECT * FROM pg_publication;"

    # WAL level
    echo -e "\n--- WAL Level (должен быть logical) ---"
    kubectl exec -n $NAMESPACE "$POSTGRES_POD" -- psql -U postgres -c "SHOW wal_level;"
fi

echo -e "\n=== 6. БЫСТРЫЕ КОМАНДЫ ДЛЯ РУЧНОЙ ПРОВЕРКИ ==="
cat << 'EOF'

# Войти в Kafka pod
kubectl exec -it -n NAMESPACE kafka-0 -- bash

# Список топиков
kafka-topics.sh --bootstrap-server localhost:9092 --list

# Читать сообщения из топика
kafka-console-consumer.sh --bootstrap-server localhost:9092 \
  --topic YOUR_TOPIC \
  --from-beginning

# Описание топика
kafka-topics.sh --bootstrap-server localhost:9092 \
  --describe --topic YOUR_TOPIC

# Удалить топик (если нужно)
kafka-topics.sh --bootstrap-server localhost:9092 \
  --delete --topic YOUR_TOPIC

# Проверить Kafka Connect коннекторы
curl http://localhost:8083/connectors
curl http://localhost:8083/connectors/YOUR_CONNECTOR/status

# Перезапустить коннектор
curl -X POST http://localhost:8083/connectors/YOUR_CONNECTOR/restart

# Удалить коннектор
curl -X DELETE http://localhost:8083/connectors/YOUR_CONNECTOR

# PostgreSQL - проверить слоты репликации
kubectl exec -it postgres-0 -n NAMESPACE -- \
  psql -U postgres -c "SELECT slot_name, plugin, slot_type, active FROM pg_replication_slots;"

# Удалить застрявший слот
kubectl exec -it postgres-0 -n NAMESPACE -- \
  psql -U postgres -c "SELECT pg_drop_replication_slot('debezium');"

EOF

echo -e "\n=== ГОТОВО ==="