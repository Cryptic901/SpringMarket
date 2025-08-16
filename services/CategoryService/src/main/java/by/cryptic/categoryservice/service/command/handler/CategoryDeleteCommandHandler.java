package by.cryptic.categoryservice.service.command.handler;

import by.cryptic.categoryservice.model.write.Category;
import by.cryptic.categoryservice.repository.write.CategoryRepository;
import by.cryptic.categoryservice.service.command.CategoryDeleteCommand;
import by.cryptic.utils.CommandHandler;
import by.cryptic.utils.event.category.CategoryDeletedEvent;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CategoryDeleteCommandHandler implements CommandHandler<CategoryDeleteCommand> {

    private final CategoryRepository categoryRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @CacheEvict(cacheNames = "categories", key = "'category:' + #result.name")
    public void handle(CategoryDeleteCommand command) {
        Category category = categoryRepository.findById(command.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category with id %s does not exists"
                        .formatted(command.categoryId())));
        categoryRepository.delete(category);
        eventPublisher.publishEvent(new CategoryDeletedEvent(command.categoryId()));
    }
}
