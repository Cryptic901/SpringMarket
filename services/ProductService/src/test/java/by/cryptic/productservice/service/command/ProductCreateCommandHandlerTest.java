package by.cryptic.productservice.service.command;

import by.cryptic.productservice.model.write.Product;
import by.cryptic.productservice.repository.write.ProductRepository;
import by.cryptic.productservice.service.command.handler.ProductCreateCommandHandler;
import by.cryptic.utils.event.product.ProductCreatedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.startsWith;

@ExtendWith(MockitoExtension.class)
class ProductCreateCommandHandlerTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private ProductCreateCommandHandler productCreateCommandHandler;

    @Test
    void createProduct_whenFieldsAreOk_shouldSaveProduct() {
        //Arrange
        UUID categoryId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Product product = Product.builder()
                .id(productId)
                .categoryId(categoryId)
                .name("testProduct")
                .description("testDesc")
                .image("test/img")
                .quantity(42)
                .price(BigDecimal.TEN)
                .build();
        ProductCreateCommand productCreateCommand = new ProductCreateCommand(
                product.getName(),
                UUID.randomUUID(),
                product.getPrice(),
                product.getQuantity(),
                product.getDescription(),
                product.getImage(),
                categoryId);
        Mockito.when(productRepository.save(any(Product.class))).thenReturn(product);
        Mockito.when(cacheManager.getCache("products")).thenReturn(cache);
        //Act
        productCreateCommandHandler.handle(productCreateCommand);
        //Assert
        Mockito.verify(cacheManager).getCache("products");
        Mockito.verify(cache, Mockito.times(1)).put(startsWith("product:"), any());
        Mockito.verify(productRepository, Mockito.times(1)).save(any(Product.class));
        Mockito.verify(applicationEventPublisher, Mockito.times(1)).publishEvent(any(ProductCreatedEvent.class));
    }
}