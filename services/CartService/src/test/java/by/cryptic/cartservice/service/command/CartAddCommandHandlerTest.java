package by.cryptic.cartservice.service.command;

import by.cryptic.cartservice.client.ProductServiceClient;
import by.cryptic.cartservice.model.write.Cart;
import by.cryptic.cartservice.publisher.CartEventPublisher;
import by.cryptic.cartservice.repository.write.CartRepository;
import by.cryptic.cartservice.service.command.handler.CartAddCommandHandler;
import by.cryptic.cartservice.util.CartUtil;
import by.cryptic.utils.DTO.ProductDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CartAddCommandHandlerTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartEventPublisher cartEventPublisher;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @Mock
    private ProductServiceClient productServiceClient;

    @Mock
    private CartUtil cartUtil;

    @InjectMocks
    private CartAddCommandHandler cartCreateCommandHandler;

    @Test
    void createCart_whenFieldsAreOk_shouldSaveCart() {
        //Arrange
        UUID cartId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Cart cart = Cart.builder()
                .id(cartId)
                .items(new ArrayList<>())
                .build();
        CartAddCommand cartAddCommand = new CartAddCommand(productId, userId);
        ProductDTO productDTO = new ProductDTO("name", BigDecimal.ONE,
                42, "desc", "image/url", UUID.randomUUID());
        Mockito.when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        Mockito.when(cacheManager.getCache("carts")).thenReturn(cache);
        Mockito.when(productServiceClient.getProductById(productId))
                .thenReturn(ResponseEntity.ok(productDTO));
        Mockito.when(cartUtil.getTotalPrice(cart.getItems())).thenReturn(BigDecimal.ZERO);
        //Act
        cartCreateCommandHandler.handle(cartAddCommand);
        //Assert
        Mockito.verify(cartRepository, Mockito.times(2)).save(any(Cart.class));
    }
}