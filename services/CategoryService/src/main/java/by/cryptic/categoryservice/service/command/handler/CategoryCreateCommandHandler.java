package by.cryptic.categoryservice.service.command.handler;

import by.cryptic.categoryservice.model.write.Category;
import by.cryptic.categoryservice.repository.write.CategoryRepository;
import by.cryptic.categoryservice.service.command.CategoryCreateCommand;
import by.cryptic.utils.CommandHandler;
import by.cryptic.utils.event.category.CategoryCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CategoryCreateCommandHandler implements CommandHandler<CategoryCreateCommand> {

    private final CategoryRepository categoryRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    @CachePut(cacheNames = "categories", key = "'category:' + #categoryDTO.name()")
    public void handle(CategoryCreateCommand categoryDTO) {
        Category category = Category.builder()
                .description(categoryDTO.description())
                .name(categoryDTO.name())
                .build();
        categoryRepository.save(category);
        eventPublisher.publishEvent(CategoryCreatedEvent.builder()
                .categoryId(category.getId())
                .description(categoryDTO.description())
                .name(categoryDTO.name())
                .build());
    }
}
