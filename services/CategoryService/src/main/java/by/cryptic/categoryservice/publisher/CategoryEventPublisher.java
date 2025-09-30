package by.cryptic.categoryservice.publisher;

import by.cryptic.categoryservice.model.write.Category;
import by.cryptic.categoryservice.model.write.OutboxEntity;
import by.cryptic.categoryservice.repository.write.OutboxRepository;
import by.cryptic.categoryservice.service.command.CategoryDeleteCommand;
import by.cryptic.utils.event.EventType;
import by.cryptic.utils.event.category.CategoryCreatedEvent;
import by.cryptic.utils.event.category.CategoryDeletedEvent;
import by.cryptic.utils.event.category.CategoryUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryEventPublisher {

    private final OutboxRepository outboxRepository;

    public void saveCategoryView(Category category) {
        OutboxEntity outboxEntity = OutboxEntity.builder()
                .aggregateId(category.getId())
                .aggregateType("category")
                .eventType(String.valueOf(EventType.CategoryCreatedEvent))
                .payload(CategoryCreatedEvent.builder()
                        .categoryId(category.getId())
                        .description(category.getDescription())
                        .name(category.getName())
                        .build())
                .build();
        outboxRepository.save(outboxEntity);
    }

    public void deleteCategoryView(CategoryDeleteCommand command) {
        OutboxEntity outboxEntity = OutboxEntity.builder()
                .aggregateId(command.categoryId())
                .aggregateType("category")
                .eventType(String.valueOf(EventType.CategoryDeletedEvent))
                .payload(CategoryDeletedEvent.builder()
                        .categoryId(command.categoryId())
                        .build())
                .build();
        outboxRepository.save(outboxEntity);
    }

    public void updateCategoryView(Category category) {
        OutboxEntity outboxEntity = OutboxEntity.builder()
                .aggregateId(category.getId())
                .aggregateType("category")
                .eventType(String.valueOf(EventType.CategoryUpdatedEvent))
                .payload(CategoryUpdatedEvent.builder()
                        .name(category.getName())
                        .categoryId(category.getId())
                        .description(category.getDescription())
                        .build())
                .build();
        outboxRepository.save(outboxEntity);
    }
}
