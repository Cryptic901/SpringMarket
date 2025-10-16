package by.cryptic.cartservice.client;

import by.cryptic.cartservice.service.command.CartAddCommand;
import by.cryptic.exceptions.CreatingException;
import by.cryptic.utils.DTO.ProductDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceAdapter {

    private final ProductServiceClient productServiceClient;

    @CircuitBreaker(name = "productCircuitBreaker", fallbackMethod = "productClientCircuitBreakerFallback")
    public ProductDTO getProductDTO(CartAddCommand command) {
        ProductDTO product = productServiceClient.getProductById(command.productId()).getBody();
        if (product == null) {
            throw new EntityNotFoundException("Product with id %s not found"
                    .formatted(command.productId()));
        }
        return product;
    }

    public ProductDTO productClientCircuitBreakerFallback(CartAddCommand command, Throwable t) {
        log.error("Failed to add {} after all attempts to cart. Cause: {}", command.productId(), t.getMessage(), t);
        throw new CreatingException("Failed to add product:" + command.productId(), t);
    }
}
