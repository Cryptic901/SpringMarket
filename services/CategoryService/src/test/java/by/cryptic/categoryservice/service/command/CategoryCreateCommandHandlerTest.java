package by.cryptic.categoryservice.service.command;

import by.cryptic.categoryservice.model.write.Category;
import by.cryptic.categoryservice.repository.write.CategoryRepository;
import by.cryptic.categoryservice.service.command.handler.CategoryCreateCommandHandler;
import by.cryptic.utils.event.category.CategoryCreatedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CategoryCreateCommandHandlerTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

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
        //Act
        categoryCreateCommandHandler.handle(categoryCreateCommand);
        //Assert
        Mockito.verify(categoryRepository, Mockito.times(1)).save(any(Category.class));
        Mockito.verify(applicationEventPublisher, Mockito.times(1)).publishEvent(any(CategoryCreatedEvent.class));
    }
}