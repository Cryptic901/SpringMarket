//package by.cryptic.orderservice.service.command;
//
//import by.cryptic.productservice.mapper.ProductMapper;
//import by.cryptic.productservice.model.write.Product;
//import by.cryptic.productservice.repository.write.ProductRepository;
//import by.cryptic.productservice.service.command.product.ProductUpdateCommand;
//import by.cryptic.productservice.service.command.product.handler.ProductUpdateCommandHandler;
//import by.cryptic.utils.event.product.ProductUpdatedEvent;
//import jakarta.persistence.EntityNotFoundException;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.cache.Cache;
//import org.springframework.cache.CacheManager;
//import org.springframework.context.ApplicationEventPublisher;
//
//import java.math.BigDecimal;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.startsWith;
//
//@ExtendWith(MockitoExtension.class)
//class ProductUpdateCommandHandlerTest {
//
//    @Mock
//    private ProductRepository productRepository;
//
//    @Mock
//    private ApplicationEventPublisher applicationEventPublisher;
//
//    @Mock
//    private ProductMapper productMapper;
//
//    @Mock
//    private CacheManager cacheManager;
//
//    @Mock
//    private Cache cache;
//
//    @InjectMocks
//    private ProductUpdateCommandHandler productUpdateCommandHandler;
//
//    @Test
//    void updateProduct_whenProductIsExists_shouldDeleteProduct() {
//        //Arrange
//        UUID categoryId = UUID.randomUUID();
//        UUID productId = UUID.randomUUID();
//        UUID userId = UUID.randomUUID();
//        Product product = Product.builder()
//                .id(productId)
//                .categoryId(categoryId)
//                .name("testProduct")
//                .description("testDesc")
//                .image("test/img")
//                .quantity(42)
//                .createdBy(userId)
//                .price(BigDecimal.TEN)
//                .build();
//        ProductUpdateCommand productUpdateCommand = new ProductUpdateCommand(productId,
//                "newTitle", null, null, null, null, userId, categoryId);
//        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(product));
//        Mockito.doNothing().when(productMapper).updateEntity(product, productUpdateCommand);
//        Mockito.when(cacheManager.getCache("products")).thenReturn(cache);
//        //Act
//        productUpdateCommandHandler.handle(productUpdateCommand);
//        //Assert
//        Mockito.verify(productRepository, Mockito.times(1)).findById(productId);
//        Mockito.verify(productRepository, Mockito.times(1)).save(any(Product.class));
//        Mockito.verify(cacheManager).getCache("products");
//        Mockito.verify(cache, Mockito.times(1)).put(startsWith("product:"), any());
//        Mockito.verify(productMapper, Mockito.times(1)).updateEntity(product, productUpdateCommand);
//        Mockito.verify(applicationEventPublisher, Mockito.times(1)).publishEvent(any(ProductUpdatedEvent.class));
//        Mockito.verifyNoMoreInteractions(productRepository, productMapper, applicationEventPublisher);
//    }
//
//    @Test
//    void updateProduct_whenProductIsNotExists_shouldThrowEntityNotFoundException() {
//        //Arrange
//        UUID productId = UUID.randomUUID();
//        UUID userId = UUID.randomUUID();
//        UUID categoryId = UUID.randomUUID();
//        ProductUpdateCommand productUpdateCommand = new ProductUpdateCommand(productId,
//                "newTitle", null, null, null, null, userId, categoryId);
//        //Act
//        //Assert
//        assertThrows(EntityNotFoundException.class, () -> productUpdateCommandHandler.handle(productUpdateCommand));
//    }
//
//    @Test
//    void updateProduct_whenUserIsNotProductCreator_shouldThrowIllegalCallerException() {
//        //Arrange
//        UUID categoryId = UUID.randomUUID();
//        UUID productId = UUID.randomUUID();
//        UUID userId = UUID.randomUUID();
//        UUID userId2 = UUID.randomUUID();
//        Product product = Product.builder()
//                .id(productId)
//                .categoryId(categoryId)
//                .name("testProduct")
//                .description("testDesc")
//                .image("test/img")
//                .quantity(42)
//                .createdBy(userId)
//                .price(BigDecimal.TEN)
//                .build();
//        ProductUpdateCommand productUpdateCommand = new ProductUpdateCommand(productId,
//                "newTitle", null, null, null, null, userId2, categoryId);
//        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(product));
//        //Act
//        //Assert
//        assertThrows(IllegalCallerException.class, () -> productUpdateCommandHandler.handle(productUpdateCommand));
//    }
//}