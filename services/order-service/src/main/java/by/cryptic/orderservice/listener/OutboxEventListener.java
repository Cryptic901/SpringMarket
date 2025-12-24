package by.cryptic.orderservice.listener;


import by.cryptic.utils.event.DomainEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class OutboxEventListener {

    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "outbox-event.order",
            groupId = "outbox-consumer-group",
            containerFactory = "stringKafkaListenerFactory")
    public void listenCategoryOutbox(String json) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(json);
        if (jsonNode.isTextual()) {
            jsonNode = objectMapper.readTree(jsonNode.asText());
        }
        try {
            DomainEvent event = objectMapper.treeToValue(jsonNode, DomainEvent.class);
            kafkaTemplate.send("order-topic", event);
        } catch (Exception e) {
            log.error("Error while sending event", e);
            throw new RuntimeException(e);
        }
    }
}
