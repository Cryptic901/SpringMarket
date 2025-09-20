//package by.cryptic.cartservice.service.query;
//
//import by.cryptic.categoryservice.dto.CategoryDTO;
//import by.cryptic.categoryservice.model.read.CategoryView;
//import by.cryptic.categoryservice.model.write.Category;
//import by.cryptic.categoryservice.repository.read.CategoryViewRepository;
//import by.cryptic.categoryservice.service.query.handler.CategoryGetByIdQueryHandler;
//import jakarta.persistence.EntityNotFoundException;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.cache.Cache;
//import org.springframework.cache.CacheManager;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//@ExtendWith(MockitoExtension.class)
//@Transactional
//class CartGetByIdQueryHandlerTest {
//
//    @Mock
//    private CategoryViewRepository categoryViewRepository;
//
//    @Mock
//    private CacheManager cacheManager;
//
//    @Mock
//    private Cache cache;
//
//    @InjectMocks
//    private CategoryGetByIdQueryHandler categoryGetByIdQueryHandler;
//
//    @Test
//    void getCategoryById_withValidUUID_shouldReturnCategory() {
//        //Arrange
//        UUID categoryId = UUID.randomUUID();
//        Category category = Category.builder()
//                .id(categoryId)
//                .name("testProduct")
//                .description("testDesc")
//                .build();
//        CategoryView categoryView = new CategoryView(categoryId, category.getName(), category.getDescription());
//        CategoryDTO categoryDTO = new CategoryDTO(category.getName(), category.getDescription());
//        Mockito.when(categoryViewRepository.findById(categoryId)).thenReturn(Optional.of(categoryView));
//        Mockito.when(cacheManager.getCache("categories")).thenReturn(cache);
//        //Act
//        CategoryDTO result = categoryGetByIdQueryHandler.handle(categoryId);
//        //Assert
//        assertEquals(categoryDTO, result);
//        Mockito.verify(categoryViewRepository, Mockito.times(1)).findById(categoryId);
//        Mockito.verifyNoMoreInteractions(categoryViewRepository);
//    }
//
//    @Test
//    void getCategoryById_withInvalidUUID_shouldThrowEntityNotFoundException() {
//        //Arrange
//        UUID categoryId = UUID.randomUUID();
//        Mockito.when(categoryViewRepository.findById(categoryId)).thenReturn(Optional.empty());
//        //Act
//        //Assert
//        assertThrows(EntityNotFoundException.class, () -> categoryGetByIdQueryHandler.handle(categoryId));
//        Mockito.verify(categoryViewRepository, Mockito.times(1)).findById(categoryId);
//        Mockito.verifyNoMoreInteractions(categoryViewRepository);
//    }
//}