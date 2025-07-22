package by.cryptic.productservice.eventhandler;

import by.cryptic.utils.event.DomainEvent;
import by.cryptic.utils.event.category.CategoryEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryEventHandler {

    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;

    @EventListener
    public void handleCategoryEvent(DomainEvent event) {
        kafkaTemplate.send("category-topic", event);
    }
}
