package by.cryptic.orderservice.client;

import by.cryptic.exceptions.CreatingException;
import by.cryptic.utils.DTO.CartProductDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceAdapter {

    private final CartServiceClient cartServiceClient;

    @CircuitBreaker(name = "cartCircuitBreaker", fallbackMethod = "cartClientGetListOfCartProductsCircuitBreakerFallback")
    public List<CartProductDTO> getListOfCartProductsByFeignClient(UUID userId) {
        return cartServiceClient.getCartProductsByUserId(userId).getBody();
    }

    @CircuitBreaker(name = "cartCircuitBreaker", fallbackMethod = "cartClientRemoveAllItemsFromCartCircuitBreakerFallback")
    public void removeAllItemsFromCartByFeignClient(UUID userId) {
        cartServiceClient.removeAllItemsFromCartByUserId(userId);
    }

    public List<CartProductDTO> cartClientGetListOfCartProductsCircuitBreakerFallback(UUID userId, Throwable t) {
        log.error("Failed to create order after all retry attempts. Cause: {}", t.getMessage(), t);
        throw new CreatingException("Failed to create order", t);
    }

    public void cartClientRemoveAllItemsFromCartCircuitBreakerFallback(UUID userId, Throwable t) {
        log.error("Failed to create order after all retry attempts. Cause: {}", t.getMessage(), t);
        throw new CreatingException("Failed to create order:", t);
    }
}
