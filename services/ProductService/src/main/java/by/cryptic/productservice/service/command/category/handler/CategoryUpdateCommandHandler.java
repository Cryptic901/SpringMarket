package by.cryptic.productservice.service.command.category.handler;

import by.cryptic.utils.event.category.CategoryUpdatedEvent;
import by.cryptic.productservice.mapper.CategoryMapper;
import by.cryptic.productservice.model.write.Category;
import by.cryptic.productservice.repository.write.CategoryRepository;
import by.cryptic.productservice.service.command.category.CategoryUpdateCommand;
import by.cryptic.utils.CommandHandler;
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
