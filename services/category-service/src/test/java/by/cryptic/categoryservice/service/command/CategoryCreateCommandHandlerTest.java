package by.cryptic.categoryservice.service.command;

import by.cryptic.categoryservice.model.write.Category;
import by.cryptic.categoryservice.publisher.CategoryEventPublisher;
import by.cryptic.categoryservice.repository.write.CategoryRepository;
import by.cryptic.categoryservice.service.command.handler.CategoryCreateCommandHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CategoryCreateCommandHandlerTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryEventPublisher categoryEventPublisher;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private CategoryCreateCommandHandler categoryCreateCommandHandler;

    @Test
    void createCategory_whenFieldsAreOk_shouldSaveCategory() {
        //Arrange
        UUID categoryId = UUID.randomUUID();
        Category category = Category.builder()
                .id(categoryId)
                .name("testProduct")
                .description("testDesc")
                .build();
        CategoryCreateCommand categoryCreateCommand = new CategoryCreateCommand(category.getName(), category.getDescription());
        Mockito.when(categoryRepository.save(any(Category.class))).thenReturn(category);
        Mockito.when(cacheManager.getCache("categories")).thenReturn(cache);
        //Act
        categoryCreateCommandHandler.handle(categoryCreateCommand);
        //Assert
        Mockito.verify(categoryRepository, Mockito.times(1)).save(any(Category.class));
    }
}