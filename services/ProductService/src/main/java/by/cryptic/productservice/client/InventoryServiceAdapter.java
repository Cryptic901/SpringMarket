package by.cryptic.productservice.client;

import by.cryptic.exceptions.CreatingException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceAdapter {

    private final InventoryServiceClient inventoryServiceClient;

    @CircuitBreaker(name = "inventoryCircuitBreaker", fallbackMethod = "checkCapacityByFeignClient")
    public Boolean checkCapacityByFeignClient(Integer quantity) {
        return inventoryServiceClient.checkCapacity(quantity).getBody();
    }

    public Boolean checkCapacityByFeignClient(Integer quantity, Throwable t) {
        log.error("Failed to define is enough capacity for {} after all retry attempts. Cause: {}", quantity, t.getMessage(), t);
        throw new CreatingException("Failed to define is enough capacity:" + quantity, t);
    }
}
