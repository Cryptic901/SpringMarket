package by.cryptic.productservice.service.query;

import by.cryptic.productservice.mapper.ProductMapper;
import by.cryptic.productservice.model.read.ProductView;
import by.cryptic.productservice.model.write.Product;
import by.cryptic.productservice.repository.read.ProductViewRepository;
import by.cryptic.productservice.service.query.handler.ProductGetByIdQueryHandler;
import by.cryptic.utils.DTO.ProductDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@Transactional
class ProductGetByIdQueryHandlerTest {

    @Mock
    private ProductViewRepository productRepository;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private ProductGetByIdQueryHandler productGetByIdQueryHandler;

    @Test
    void getCategoryById_withValidUUID_shouldReturnCategory() {
        //Arrange
        UUID productId = UUID.randomUUID();
        String cacheKey = "product:" + productId;
        Mockito.when(cacheManager.getCache("products")).thenReturn(cache);
        Mockito.when(cache.get(cacheKey, Product.class)).thenReturn(null);

        UUID categoryId = UUID.randomUUID();
        Product product = Product.builder()
                .id(productId)
                .categoryId(categoryId)
                .name("testProduct")
                .description("testDesc")
                .image("test/img")
                .quantity(42)
                .price(BigDecimal.TEN)
                .build();
        ProductView productView = ProductView.builder()
                .productId(productId)
                .categoryId(categoryId)
                .name(product.getName())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .description(product.getDescription())
                .image(product.getImage())
                .build();
        ProductDTO productDTO = ProductMapper.toDto(product);
        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(productView));
        //Act
        ProductDTO result = productGetByIdQueryHandler.handle(productId);
        //Assert
        assertEquals(productDTO, result);
        Mockito.verify(cacheManager).getCache("products");
        Mockito.verify(productRepository, Mockito.times(1)).findById(productId);
        Mockito.verifyNoMoreInteractions(productRepository);
    }

    @Test
    void getCategoryById_withInvalidUUID_shouldThrowEntityNotFoundException() {
        //Arrange
        UUID productId = UUID.randomUUID();
        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.empty());
        //Act
        //Assert
        assertThrows(EntityNotFoundException.class, () -> productGetByIdQueryHandler.handle(productId));
        Mockito.verify(productRepository, Mockito.times(1)).findById(productId);
        Mockito.verifyNoMoreInteractions(productRepository);
    }
}