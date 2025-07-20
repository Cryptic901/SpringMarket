package by.cryptic.productservice.listener;

import by.cryptic.productservice.mapper.CategoryMapper;
import by.cryptic.productservice.model.read.CategoryView;
import by.cryptic.productservice.repository.read.CategoryViewRepository;
import by.cryptic.utils.event.DomainEvent;
import by.cryptic.utils.event.category.CategoryCreatedEvent;
import by.cryptic.utils.event.category.CategoryDeletedEvent;
import by.cryptic.utils.event.category.CategoryUpdatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
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
        log.info("Received event type {}", rawEvent);
        DomainEvent event = objectMapper.readValue(rawEvent, DomainEvent.class);
        switch (event) {
            case CategoryCreatedEvent categoryCreatedEvent -> categoryViewRepository.save(CategoryView.builder()
                    .name(categoryCreatedEvent.getName())
                    .categoryId(categoryCreatedEvent.getCategoryId())
                    .description(categoryCreatedEvent.getDescription())
                    .build());

            case CategoryUpdatedEvent categoryUpdatedEvent ->
                    categoryViewRepository.findById(categoryUpdatedEvent.getCategoryId()).ifPresent(categoryView -> {
                        categoryMapper.updateView(categoryView, categoryUpdatedEvent);
                        categoryViewRepository.save(categoryView);
                    });

            case CategoryDeletedEvent categoryDeletedEvent ->
                    categoryViewRepository.deleteById(categoryDeletedEvent.getCategoryId());

            default -> throw new IllegalStateException("Unexpected event type: " + event);
        }
    }
}
