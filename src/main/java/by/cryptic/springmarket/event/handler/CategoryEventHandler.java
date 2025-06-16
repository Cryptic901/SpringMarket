package by.cryptic.springmarket.event.handler;

import by.cryptic.springmarket.event.DomainEvent;
import by.cryptic.springmarket.event.category.CategoryEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryEventHandler {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @EventListener
    public void handleCategoryEvent(CategoryEvent event) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(event);
        kafkaTemplate.send("category-topic", json);
    }
}
