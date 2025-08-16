package by.cryptic.categoryservice.service.command;

import by.cryptic.categoryservice.model.write.Category;
import by.cryptic.categoryservice.repository.write.CategoryRepository;
import by.cryptic.categoryservice.service.command.handler.CategoryUpdateCommandHandler;
import by.cryptic.utils.event.category.CategoryUpdatedEvent;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CategoryUpdateCommandHandlerTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

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
                new CategoryUpdateCommand(categoryId, category.getName(), null);
        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        //Act
        categoryUpdateCommandHandler.handle(categoryUpdateCommand);
        //Assert
        Mockito.verify(categoryRepository, Mockito.times(1)).findById(categoryId);
        Mockito.verify(categoryRepository, Mockito.times(1)).save(any(Category.class));
        Mockito.verify(applicationEventPublisher, Mockito.times(1)).publishEvent(any(CategoryUpdatedEvent.class));
        Mockito.verifyNoMoreInteractions(categoryRepository, applicationEventPublisher);
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