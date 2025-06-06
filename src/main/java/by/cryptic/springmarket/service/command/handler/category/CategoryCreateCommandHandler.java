package by.cryptic.springmarket.service.command.handler.category;

import by.cryptic.springmarket.event.category.CategoryCreatedEvent;
import by.cryptic.springmarket.model.write.Category;
import by.cryptic.springmarket.service.command.CategoryCreateCommand;
import by.cryptic.springmarket.repository.write.CategoryRepository;
import by.cryptic.springmarket.service.command.handler.CommandHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CategoryCreateCommandHandler implements CommandHandler<CategoryCreateCommand> {


    private final CategoryRepository categoryRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
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
