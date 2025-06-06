package by.cryptic.springmarket.event.handler;

import by.cryptic.springmarket.event.category.CategoryEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryEventHandler {

    private final KafkaTemplate<String, CategoryEvent> kafkaTemplate;

    @EventListener
    public void handleCartEvent(CategoryEvent event) {
        kafkaTemplate.send("category-topic", event);
    }
}
