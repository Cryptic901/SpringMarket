package by.cryptic.cartservice.service.command;

import by.cryptic.cartservice.model.write.Cart;
import by.cryptic.cartservice.publisher.CartEventPublisher;
import by.cryptic.cartservice.repository.write.CartRepository;
import by.cryptic.cartservice.service.command.handler.CartClearCommandHandler;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CartClearCommandHandlerTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartEventPublisher cartEventPublisher;

    @InjectMocks
    private CartClearCommandHandler cartClearCommandHandler;

    @Test
    void clearCart_whenCartIsExists_shouldClearCart() {
        //Arrange
        UUID cartId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Cart cart = Cart.builder()
                .id(cartId)
                .items(new ArrayList<>())
                .build();
        CartClearCommand cartClearCommand =
                new CartClearCommand(userId);
        Mockito.when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.of(cart));
        //Act
        cartClearCommandHandler.handle(cartClearCommand);
        //Assert
        Mockito.verify(cartRepository, Mockito.times(1)).findByUserIdWithItems(userId);
        Mockito.verifyNoMoreInteractions(cartRepository);
    }

    @Test
    void updateCart_whenCartIsNotExists_shouldThrowEntityNotFoundException() {
        //Arrange
        CartClearCommand cartUpdateCommand = new CartClearCommand(UUID.randomUUID());
        //Act
        //Assert
        assertThrows(EntityNotFoundException.class, () -> cartClearCommandHandler.handle(cartUpdateCommand));
    }
}