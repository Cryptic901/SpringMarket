package by.cryptic.cartservice.service.command;

import by.cryptic.cartservice.model.write.Cart;
import by.cryptic.cartservice.model.write.CartProduct;
import by.cryptic.cartservice.publisher.CartEventPublisher;
import by.cryptic.cartservice.repository.write.CartProductRepository;
import by.cryptic.cartservice.repository.write.CartRepository;
import by.cryptic.cartservice.service.command.handler.CartDeleteProductCommandHandler;
import by.cryptic.cartservice.util.CartUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CartDeleteProductCommandHandlerTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartProductRepository cartProductRepository;

    @Mock
    private CartUtil cartUtil;

    @Mock
    private CartEventPublisher cartEventPublisher;

    @InjectMocks
    private CartDeleteProductCommandHandler cartDeleteProductCommandHandler;

    @Test
    void deleteCartProduct_whenCartProductIsExists_shouldDeleteCartProduct() {
        //Arrange
        UUID cartId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Cart cart = Cart.builder()
                .id(cartId)
                .items(new ArrayList<>())
                .build();
        cart.setItems(List.of(new CartProduct(UUID.randomUUID(), 42, BigDecimal.ONE,
                cart, productId)));
        CartDeleteProductCommand cartDeleteProductCommand = new CartDeleteProductCommand(productId, userId);
        Mockito.when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.of(cart));
        Mockito.when(cartUtil.getTotalPrice(cart.getItems())).thenReturn(BigDecimal.ZERO);
        Mockito.doNothing().when(cartEventPublisher).deleteCartView(any());
        //Act
        cartDeleteProductCommandHandler.handle(cartDeleteProductCommand);
        //Assert
        Mockito.verify(cartRepository, Mockito.times(1)).findByUserIdWithItems(userId);
        Mockito.verify(cartUtil, Mockito.times(1)).getTotalPrice(cart.getItems());
        Mockito.verify(cartEventPublisher, Mockito.times(1)).deleteCartView(any());
        Mockito.verifyNoMoreInteractions(cartRepository, cartUtil, cartEventPublisher);
    }

    @Test
    void deleteCartProduct_whenCartProductIsNotExists_shouldThrowEntityNotFoundException() {
        //Arrange
        CartDeleteProductCommand productDeleteProductCommand =
                new CartDeleteProductCommand(UUID.randomUUID(), UUID.randomUUID());
        //Act
        //Assert
        assertThrows(EntityNotFoundException.class, () -> cartDeleteProductCommandHandler.handle(productDeleteProductCommand));
    }
}