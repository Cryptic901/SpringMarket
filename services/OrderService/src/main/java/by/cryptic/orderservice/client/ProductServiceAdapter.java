package by.cryptic.orderservice.client;

import by.cryptic.exceptions.CreatingException;
import by.cryptic.utils.DTO.ProductDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceAdapter {

    private final ProductServiceClient productServiceClient;

    @CircuitBreaker(name = "productCircuitBreaker", fallbackMethod = "productClientCircuitBreakerFallback")
    public ProductDTO getProductByFeignClient(UUID productId) {
        return productServiceClient.getProductById(productId).getBody();
    }


    public ProductDTO productClientCircuitBreakerFallback(UUID productId, Throwable t) {
        log.error("Failed to create order with product id {} after all retry attempts. Cause: {}", productId, t.getMessage(), t);
        throw new CreatingException("Failed to create order with productId:" + productId, t);
    }
}
