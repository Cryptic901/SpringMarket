package by.cryptic.cartservice.service.query;

import by.cryptic.cartservice.model.read.CartProductView;
import by.cryptic.cartservice.model.read.CartView;
import by.cryptic.cartservice.repository.read.CartViewRepository;
import by.cryptic.cartservice.service.query.handler.CartGetAllQueryHandler;
import by.cryptic.utils.DTO.CartProductDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Profile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@Profile("mongo")
class CartGetAllQueryHandlerTest {

    @Mock
    private CartViewRepository cartViewRepository;

    @InjectMocks
    private CartGetAllQueryHandler cartGetAllQueryHandler;

    @Test
    void getAllCarts_whenCartsExists_shouldReturnAllCarts() {
        //Arrange
        UUID cartId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        CartProductDTO cartProductDTO = new CartProductDTO();
        CartProductView cartProductView = new CartProductView();
        CartView cartView = CartView.builder()
                .cartId(cartId)
                .userId(userId)
                .products(List.of(cartProductView))
                .build();
        Mockito.when(cartViewRepository.findCartViewByUserId(userId)).thenReturn(Optional.of(cartView));
        //Act
        List<CartProductDTO> result = cartGetAllQueryHandler.handle(new CartGetAllQuery(userId));
        //Assert
        assertEquals(Collections.singletonList(cartProductDTO), result);
        Mockito.verify(cartViewRepository, Mockito.times(1)).findCartViewByUserId(userId);
        Mockito.verifyNoMoreInteractions(cartViewRepository);
    }

    @Test
    void getAllCarts_whenCartsDoesNotExists_shouldReturnEntityNotFoundException() {
        //Arrange
        //Act
        //Assert
        assertThrows(EntityNotFoundException.class, () ->
                cartGetAllQueryHandler.handle(new CartGetAllQuery(UUID.randomUUID())));
    }
}