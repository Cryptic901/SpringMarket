//package by.cryptic.orderservice.service.command;
//
//import by.cryptic.productservice.model.write.Product;
//import by.cryptic.productservice.repository.write.ProductRepository;
//import by.cryptic.productservice.service.command.product.ProductDeleteCommand;
//import by.cryptic.productservice.service.command.product.handler.ProductDeleteCommandHandler;
//import by.cryptic.utils.event.product.ProductDeletedEvent;
//import jakarta.persistence.EntityNotFoundException;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.context.ApplicationEventPublisher;
//
//import java.math.BigDecimal;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//
//@ExtendWith(MockitoExtension.class)
//class ProductDeleteCommandHandlerTest {
//
//    @Mock
//    private ProductRepository productRepository;
//
//    @Mock
//    private ApplicationEventPublisher applicationEventPublisher;
//
//    @InjectMocks
//    private ProductDeleteCommandHandler productDeleteCommandHandler;
//
//    @Test
//    void deleteProduct_whenProductIsExists_shouldDeleteProduct() {
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
//        ProductDeleteCommand productDeleteCommand = new ProductDeleteCommand(productId, userId);
//        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(product));
//        Mockito.doNothing().when(productRepository).deleteById(productId);
//        //Act
//        productDeleteCommandHandler.handle(productDeleteCommand);
//        //Assert
//        Mockito.verify(productRepository, Mockito.times(1)).findById(productId);
//        Mockito.verify(productRepository, Mockito.times(1)).deleteById(productId);
//        Mockito.verify(applicationEventPublisher, Mockito.times(1)).publishEvent(any(ProductDeletedEvent.class));
//        Mockito.verifyNoMoreInteractions(productRepository, applicationEventPublisher);
//    }
//
//    @Test
//    void deleteProduct_whenProductIsNotExists_shouldThrowEntityNotFoundException() {
//        //Arrange
//        UUID productId = UUID.randomUUID();
//        UUID userId = UUID.randomUUID();
//        ProductDeleteCommand productDeleteCommand = new ProductDeleteCommand(productId, userId);
//        //Act
//        //Assert
//        assertThrows(EntityNotFoundException.class, () -> productDeleteCommandHandler.handle(productDeleteCommand));
//    }
//
//    @Test
//    void deleteProduct_whenUserIsNotProductCreator_shouldThrowIllegalCallerException() {
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
//        ProductDeleteCommand productDeleteCommand = new ProductDeleteCommand(productId, userId2);
//        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(product));
//        //Act
//        //Assert
//        assertThrows(IllegalCallerException.class, () -> productDeleteCommandHandler.handle(productDeleteCommand));
//    }
//}