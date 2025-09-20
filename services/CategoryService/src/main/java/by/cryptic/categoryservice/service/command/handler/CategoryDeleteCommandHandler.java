package by.cryptic.categoryservice.service.command.handler;

import by.cryptic.categoryservice.model.write.Category;
import by.cryptic.categoryservice.publisher.CategoryEventPublisher;
import by.cryptic.categoryservice.repository.write.CategoryRepository;
import by.cryptic.categoryservice.service.command.CategoryDeleteCommand;
import by.cryptic.utils.handler.CommandHandler;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CategoryDeleteCommandHandler implements CommandHandler<CategoryDeleteCommand> {

    private final CategoryRepository categoryRepository;
    private final CategoryEventPublisher categoryEventPublisher;

    @Override
    @CacheEvict(cacheNames = "categories", key = "'category:' + #command.categoryId()")
    public void handle(CategoryDeleteCommand command) {
        deleteCategory(command);
        categoryEventPublisher.deleteCategoryView(command);
    }

    private void deleteCategory(CategoryDeleteCommand command) {
        Category category = categoryRepository.findById(command.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category with id %s does not exists"
                        .formatted(command.categoryId())));
        categoryRepository.delete(category);
    }
}
