package by.cryptic.cartservice.service.query;

import by.cryptic.cartservice.model.read.CartProductView;
import by.cryptic.cartservice.model.read.CartView;
import by.cryptic.cartservice.repository.read.CartViewRepository;
import by.cryptic.cartservice.service.query.handler.CartGetByIdQueryHandler;
import by.cryptic.utils.DTO.CartProductDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@Transactional
@Profile("mongo")
class CartGetByIdQueryHandlerTest {

    @Mock
    private CartViewRepository cartViewRepository;

    @InjectMocks
    private CartGetByIdQueryHandler cartGetByIdQueryHandler;

    @Test
    void getCartById_withValidUUID_shouldReturnCart() {
        //Arrange
        UUID userId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        CartView cartView = new CartView(cartId, userId, BigDecimal.ONE,
                List.of(new CartProductView(productId, 42, BigDecimal.ONE)));
        CartProductDTO cartProductDTO = CartProductDTO.builder()
                .quantity(42)
                .productId(productId)
                .pricePerUnit(BigDecimal.ONE)
                .build();
        Mockito.when(cartViewRepository.findCartViewByUserId(userId)).thenReturn(Optional.of(cartView));
        //Act
        CartProductDTO result = cartGetByIdQueryHandler.handle(new CartGetByIdQuery(userId, productId));
        //Assert
        Assertions.assertEquals(result, cartProductDTO);
        Mockito.verify(cartViewRepository, Mockito.times(1)).findCartViewByUserId(userId);
        Mockito.verifyNoMoreInteractions(cartViewRepository);
    }

    @Test
    void getCartById_withInvalidUUID_shouldThrowEntityNotFoundException() {
        //Arrange
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        //Act
        //Assert
        assertThrows(EntityNotFoundException.class, () -> cartGetByIdQueryHandler.handle(new CartGetByIdQuery(userId, productId)));
        Mockito.verify(cartViewRepository, Mockito.times(1)).findCartViewByUserId(userId);
        Mockito.verifyNoMoreInteractions(cartViewRepository);
    }
}