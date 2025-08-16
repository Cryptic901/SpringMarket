package by.cryptic.categoryservice.service.query;

import by.cryptic.categoryservice.dto.CategoryDTO;
import by.cryptic.categoryservice.mapper.CategoryMapper;
import by.cryptic.categoryservice.model.read.CategoryView;
import by.cryptic.categoryservice.repository.read.CategoryViewRepository;
import by.cryptic.categoryservice.service.query.handler.CategoryGetAllQueryHandler;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Profile;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@Profile("mongo")
class CategoryGetAllQueryHandlerTest {

    @Mock
    private CategoryViewRepository categoryViewRepository;

    @InjectMocks
    private CategoryGetAllQueryHandler categoryGetAllQueryHandler;

    @Test
    void getAllCategories_whenCategoriesExists_shouldReturnAllCategories() {
        //Arrange
        UUID categoryId = UUID.randomUUID();
        CategoryView categoryView = CategoryView.builder()
                .categoryId(categoryId)
                .name("testProduct")
                .description("testDesc")
                .build();
        CategoryDTO categoryDTO = CategoryMapper.toDto(categoryView);
        Mockito.when(categoryViewRepository.findAll()).thenReturn(Collections.singletonList(categoryView));
        //Act
        List<CategoryDTO> result = categoryGetAllQueryHandler.handle(new CategoryGetAllQuery());
        //Assert
        assertEquals(Collections.singletonList(categoryDTO), result);
        Mockito.verify(categoryViewRepository, Mockito.times(1)).findAll();
        Mockito.verifyNoMoreInteractions(categoryViewRepository);
    }

    @Test
    void getAllCategories_shouldReturnEntityNotFoundException() {
        //Arrange
        Mockito.when(categoryViewRepository.findAll()).thenReturn(Collections.emptyList());
        //Act
        //Assert
        assertThrows(EntityNotFoundException.class, () -> categoryGetAllQueryHandler.handle(new CategoryGetAllQuery()));
        Mockito.verify(categoryViewRepository, Mockito.times(1)).findAll();
        Mockito.verifyNoMoreInteractions(categoryViewRepository);
    }
}