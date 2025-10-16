package by.cryptic.productservice.service.command.handler;

import by.cryptic.exceptions.CreatingException;
import by.cryptic.exceptions.InsufficientWarehouseCapacityException;
import by.cryptic.productservice.client.CategoryServiceAdapter;
import by.cryptic.productservice.client.InventoryServiceAdapter;
import by.cryptic.productservice.model.write.Product;
import by.cryptic.productservice.publisher.ProductEventPublisher;
import by.cryptic.productservice.repository.write.ProductRepository;
import by.cryptic.productservice.service.command.ProductCreateCommand;
import by.cryptic.utils.enums.ProductStatus;
import by.cryptic.utils.handler.CommandHandler;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCreateCommandHandler implements CommandHandler<ProductCreateCommand> {

    private final ProductRepository productRepository;
    private final CacheManager cacheManager;
    private final ProductEventPublisher productEventPublisher;
    private final CategoryServiceAdapter categoryServiceAdapter;
    private final InventoryServiceAdapter inventoryServiceAdapter;

    @Override
    @Transactional
    @Retry(name = "productRetry", fallbackMethod = "productCreateRetryFallback")
    public void handle(ProductCreateCommand productDTO) {
        if (categoryServiceAdapter.getCategoryByFeignClient(productDTO.categoryId()) == null) {
            throw new EntityNotFoundException("Category not found with id:" + productDTO.categoryId());
        }

        if (!inventoryServiceAdapter.checkCapacityByFeignClient(productDTO.quantity())) {
            throw new InsufficientWarehouseCapacityException("Insufficient warehouse " +
                    "capacity for " + productDTO.quantity());
        }
        Product product = Product.builder()
                .name(productDTO.name())
                .description(productDTO.description())
                .quantity(productDTO.quantity())
                .price(productDTO.price())
                .image(productDTO.image())
                .categoryId(productDTO.categoryId())
                .productStatus(ProductStatus.ACTIVE)
                .build();
        productRepository.save(product);

        productEventPublisher.saveProductView(product);
        updateCache(product);
    }

    private void updateCache(Product product) {
        try {
            Objects.requireNonNull(cacheManager.getCache("products"))
                    .put("product:" + product.getId(), product);
        } catch (Exception e) {
            log.warn("Failed to update product cache {}", product.getId(), e);
        }
    }

    public void productCreateRetryFallback(ProductCreateCommand productCreateCommand, Throwable t) {
        log.error("Failed to create {} after all retry attempts. Cause: {}", productCreateCommand.name(), t.getMessage(), t);
        throw new CreatingException("Failed to create product: " + productCreateCommand.name(), t);
    }
}
