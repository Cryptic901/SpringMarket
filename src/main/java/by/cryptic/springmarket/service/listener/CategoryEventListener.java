package by.cryptic.springmarket.service.listener;

import by.cryptic.springmarket.enums.EventType;
import by.cryptic.springmarket.event.category.CategoryCreatedEvent;
import by.cryptic.springmarket.event.category.CategoryDeletedEvent;
import by.cryptic.springmarket.event.category.CategoryUpdatedEvent;
import by.cryptic.springmarket.mapper.CategoryMapper;
import by.cryptic.springmarket.model.read.CategoryView;
import by.cryptic.springmarket.repository.read.CategoryViewRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class CategoryEventListener {

    private final ObjectMapper objectMapper;
    private final CategoryViewRepository categoryViewRepository;
    private final CategoryMapper categoryMapper;

    @KafkaListener(topics = "category-topic", groupId = "category-group")
    public void listenCategory(String rawEvent) throws JsonProcessingException {
        JsonNode node = objectMapper.readTree(rawEvent);
        String type = node.get("eventType").asText();
        log.info("Received event type {}", type);
        switch (EventType.valueOf(type)) {
            case CategoryCreatedEvent -> {
                CategoryCreatedEvent event = objectMapper.treeToValue(node, CategoryCreatedEvent.class);
                categoryViewRepository.save(CategoryView.builder()
                        .name(event.getName())
                        .categoryId(event.getCategoryId())
                        .description(event.getDescription())
                        .build());
            }
            case CategoryUpdatedEvent -> {
                CategoryUpdatedEvent event = objectMapper.treeToValue(node, CategoryUpdatedEvent.class);
                categoryViewRepository.findById(event.getCategoryId()).ifPresent(categoryView -> {
                    categoryMapper.updateView(categoryView, event);
                    categoryViewRepository.save(categoryView);
                });
            }
            case CategoryDeletedEvent -> {
                CategoryDeletedEvent event = objectMapper.treeToValue(node, CategoryDeletedEvent.class);
                categoryViewRepository.deleteById(event.getCategoryId());
            }
            default -> throw new IllegalStateException("Unexpected event type: " + type);
        }
    }
}
