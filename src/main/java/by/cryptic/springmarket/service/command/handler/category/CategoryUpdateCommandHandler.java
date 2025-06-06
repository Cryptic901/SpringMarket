package by.cryptic.springmarket.service.command.handler.category;

import by.cryptic.springmarket.event.category.CategoryUpdatedEvent;
import by.cryptic.springmarket.mapper.CategoryMapper;
import by.cryptic.springmarket.model.write.Category;
import by.cryptic.springmarket.repository.write.CategoryRepository;
import by.cryptic.springmarket.service.command.CategoryUpdateCommand;
import by.cryptic.springmarket.service.command.handler.CommandHandler;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CategoryUpdateCommandHandler implements CommandHandler<CategoryUpdateCommand> {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void handle(CategoryUpdateCommand command) {
        Category category = categoryRepository.findById(command.categoryId())
                .orElseThrow(() -> new EntityNotFoundException
                        ("Category not found with id: %s".formatted(command.categoryId())));
        categoryMapper.updateEntity(category, command);
        categoryRepository.save(category);
        eventPublisher.publishEvent(CategoryUpdatedEvent.builder()
                .name(command.name())
                .categoryId(category.getId())
                .description(command.description())
                .build());
    }
}
