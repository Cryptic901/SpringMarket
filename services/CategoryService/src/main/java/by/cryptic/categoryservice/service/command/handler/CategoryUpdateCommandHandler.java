package by.cryptic.categoryservice.service.command.handler;

import by.cryptic.categoryservice.mapper.CategoryMapper;
import by.cryptic.categoryservice.model.write.Category;
import by.cryptic.categoryservice.repository.write.CategoryRepository;
import by.cryptic.categoryservice.service.command.CategoryUpdateCommand;
import by.cryptic.utils.CommandHandler;
import by.cryptic.utils.event.category.CategoryUpdatedEvent;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CategoryUpdateCommandHandler implements CommandHandler<CategoryUpdateCommand> {

    private final CategoryRepository categoryRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    @CachePut(cacheNames = "categories", key = "'category:' + #command.name()")
    public void handle(CategoryUpdateCommand command) {
        Category category = categoryRepository.findById(command.categoryId())
                .orElseThrow(() -> new EntityNotFoundException
                        ("Category not found with id: %s".formatted(command.categoryId())));
        CategoryMapper.updateEntity(category, command);
        categoryRepository.save(category);
        eventPublisher.publishEvent(CategoryUpdatedEvent.builder()
                .name(command.name())
                .categoryId(category.getId())
                .description(command.description())
                .build());
    }
}
