package by.cryptic.productservice.client;

import by.cryptic.exceptions.CreatingException;
import by.cryptic.utils.DTO.CategoryDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceAdapter {

    private final CategoryServiceClient categoryServiceClient;

    @CircuitBreaker(name = "categoryCircuitBreaker", fallbackMethod = "categoryClientCircuitBreakerFallback")
    public CategoryDTO getCategoryByFeignClient(UUID categoryId) {
        return categoryServiceClient.getCategoryById(categoryId).getBody();
    }

    public CategoryDTO categoryClientCircuitBreakerFallback(UUID categoryId, Throwable t) {
        log.error("Failed to find category {} after all retry attempts. Cause: {}", categoryId, t.getMessage(), t);
        throw new CreatingException("Failed to find category with categoryId:" + categoryId, t);
    }
}
