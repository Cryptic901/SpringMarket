package by.cryptic.categoryservice.service.command;

import by.cryptic.categoryservice.model.write.Category;
import by.cryptic.categoryservice.publisher.CategoryEventPublisher;
import by.cryptic.categoryservice.repository.write.CategoryRepository;
import by.cryptic.categoryservice.service.command.handler.CategoryUpdateCommandHandler;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CategoryUpdateCommandHandlerTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryEventPublisher categoryEventPublisher;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private CategoryUpdateCommandHandler categoryUpdateCommandHandler;

    @Test
    void updateCategory_whenCategoryIsExists_shouldUpdateCategory() {
        //Arrange
        UUID categoryId = UUID.randomUUID();
        Category category = Category.builder()
                .id(categoryId)
                .name("testProduct")
                .description("testDesc")
                .build();
        CategoryUpdateCommand categoryUpdateCommand =
                new CategoryUpdateCommand(categoryId, category.getName(), "new Desc");
        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        Mockito.when(categoryRepository.save(any())).thenReturn(category);
        Mockito.when(cacheManager.getCache(any())).thenReturn(cache);
        //Act
        categoryUpdateCommandHandler.handle(categoryUpdateCommand);
        //Assert
        Mockito.verify(categoryRepository, Mockito.times(1)).findById(categoryId);
        Mockito.verify(categoryRepository, Mockito.times(1)).save(any(Category.class));
        Mockito.verify(categoryEventPublisher, Mockito.times(1)).updateCategoryView(any(Category.class));
        Mockito.verifyNoMoreInteractions(categoryRepository, categoryEventPublisher);
    }

    @Test
    void updateCategory_whenCategoryIsNotExists_shouldThrowEntityNotFoundException() {
        //Arrange
        UUID categoryId = UUID.randomUUID();
        CategoryUpdateCommand categoryUpdateCommand = new CategoryUpdateCommand(categoryId,
                null, null);
        //Act
        //Assert
        assertThrows(EntityNotFoundException.class, () -> categoryUpdateCommandHandler.handle(categoryUpdateCommand));
    }
}