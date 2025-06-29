package by.cryptic.productservice.service.command.category.handler;

import by.cryptic.utils.event.category.CategoryCreatedEvent;
import by.cryptic.productservice.model.write.Category;
import by.cryptic.productservice.repository.write.CategoryRepository;
import by.cryptic.productservice.service.command.category.CategoryCreateCommand;
import by.cryptic.utils.CommandHandler;
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
