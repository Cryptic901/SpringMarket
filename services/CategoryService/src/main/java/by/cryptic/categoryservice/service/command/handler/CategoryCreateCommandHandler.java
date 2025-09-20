package by.cryptic.categoryservice.service.command.handler;

import by.cryptic.categoryservice.model.write.Category;
import by.cryptic.categoryservice.publisher.CategoryEventPublisher;
import by.cryptic.categoryservice.repository.write.CategoryRepository;
import by.cryptic.categoryservice.service.command.CategoryCreateCommand;
import by.cryptic.utils.handler.CommandHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class CategoryCreateCommandHandler implements CommandHandler<CategoryCreateCommand> {

    private final CategoryRepository categoryRepository;
    private final CacheManager cacheManager;
    private final CategoryEventPublisher categoryEventPublisher;

    @Override
    @Transactional
    public void handle(CategoryCreateCommand categoryDTO) {
        Category category = saveCategory(categoryDTO);

        categoryEventPublisher.saveCategoryView(category);

        updateCache(category);
    }

    private void updateCache(Category category) {
        Objects.requireNonNull(cacheManager.getCache("categories"))
                .put("category:" + category.getId(), category);
    }

    private Category saveCategory(CategoryCreateCommand categoryDTO) {
        Category category = Category.builder()
                .description(categoryDTO.description())
                .name(categoryDTO.name())
                .build();
        return categoryRepository.save(category);
    }
}
