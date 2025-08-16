package by.cryptic.categoryservice.listener;

import by.cryptic.categoryservice.mapper.CategoryMapper;
import by.cryptic.categoryservice.model.read.CategoryView;
import by.cryptic.categoryservice.repository.read.CategoryViewRepository;
import by.cryptic.utils.event.DomainEvent;
import by.cryptic.utils.event.category.CategoryCreatedEvent;
import by.cryptic.utils.event.category.CategoryDeletedEvent;
import by.cryptic.utils.event.category.CategoryUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class CategoryEventListener {

    private final CategoryViewRepository categoryViewRepository;

    @KafkaListener(topics = "category-topic", groupId = "category-group")
    public void listenCategory(DomainEvent event) {
        log.info("Received event type {}", event);
        switch (event) {
            case CategoryCreatedEvent categoryCreatedEvent -> categoryViewRepository.save(CategoryView.builder()
                    .name(categoryCreatedEvent.getName())
                    .categoryId(categoryCreatedEvent.getCategoryId())
                    .description(categoryCreatedEvent.getDescription())
                    .build());

            case CategoryUpdatedEvent categoryUpdatedEvent ->
                    categoryViewRepository.findById(categoryUpdatedEvent.getCategoryId()).ifPresent(categoryView -> {
                        CategoryMapper.updateView(categoryView, categoryUpdatedEvent);
                        categoryViewRepository.save(categoryView);
                    });

            case CategoryDeletedEvent categoryDeletedEvent ->
                    categoryViewRepository.deleteById(categoryDeletedEvent.getCategoryId());

            default -> throw new IllegalStateException("Unexpected event type: " + event);
        }
    }
}