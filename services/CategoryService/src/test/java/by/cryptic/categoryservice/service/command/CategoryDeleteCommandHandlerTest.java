package by.cryptic.categoryservice.service.command;

import by.cryptic.categoryservice.model.write.Category;
import by.cryptic.categoryservice.publisher.CategoryEventPublisher;
import by.cryptic.categoryservice.repository.write.CategoryRepository;
import by.cryptic.categoryservice.service.command.handler.CategoryDeleteCommandHandler;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CategoryDeleteCommandHandlerTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryEventPublisher categoryEventPublisher;

    @InjectMocks
    private CategoryDeleteCommandHandler categoryDeleteCommandHandler;

    @Test
    void deleteCategory_whenCategoryIsExists_shouldDeleteCategory() {
        //Arrange
        UUID categoryId = UUID.randomUUID();
        Category category = Category.builder()
                .id(categoryId)
                .name("testCategory")
                .description("testDesc")
                .build();
        CategoryDeleteCommand categoryDeleteCommand = new CategoryDeleteCommand(categoryId);
        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        Mockito.doNothing().when(categoryRepository).delete(any());
        //Act
        categoryDeleteCommandHandler.handle(categoryDeleteCommand);
        //Assert
        Mockito.verify(categoryRepository, Mockito.times(1)).findById(categoryId);
        Mockito.verify(categoryRepository, Mockito.times(1)).delete(any());
        Mockito.verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void deleteCategory_whenCategoryIsNotExists_shouldThrowEntityNotFoundException() {
        //Arrange
        UUID categoryId = UUID.randomUUID();
        CategoryDeleteCommand productDeleteCommand = new CategoryDeleteCommand(categoryId);
        //Act
        //Assert
        assertThrows(EntityNotFoundException.class, () -> categoryDeleteCommandHandler.handle(productDeleteCommand));
    }
}