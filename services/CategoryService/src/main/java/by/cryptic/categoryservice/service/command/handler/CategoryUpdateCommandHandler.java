package by.cryptic.categoryservice.service.command.handler;

import by.cryptic.categoryservice.mapper.CategoryMapper;
import by.cryptic.categoryservice.model.write.Category;
import by.cryptic.categoryservice.publisher.CategoryEventPublisher;
import by.cryptic.categoryservice.repository.write.CategoryRepository;
import by.cryptic.categoryservice.service.command.CategoryUpdateCommand;
import by.cryptic.utils.handler.CommandHandler;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class CategoryUpdateCommandHandler implements CommandHandler<CategoryUpdateCommand> {

    private final CategoryRepository categoryRepository;
    private final CacheManager cacheManager;
    private final CategoryEventPublisher categoryEventPublisher;

    @Override
    @Transactional
    public void handle(CategoryUpdateCommand command) {
        Category category = updateCategory(command);

        categoryEventPublisher.updateCategoryView(category);

        updateCache(category);
    }

    private void updateCache(Category category) {
        Objects.requireNonNull(cacheManager.getCache("categories"))
                .put("category:" + category.getId(), category);
    }

    private Category updateCategory(CategoryUpdateCommand command) {
        Category category = categoryRepository.findById(command.categoryId())
                .orElseThrow(() -> new EntityNotFoundException
                        ("Category not found with id: %s".formatted(command.categoryId())));

        CategoryMapper.updateEntity(category, command);
        return categoryRepository.save(category);
    }
}
