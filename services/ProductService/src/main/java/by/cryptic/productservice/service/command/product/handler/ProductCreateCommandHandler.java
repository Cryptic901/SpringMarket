package by.cryptic.productservice.service.command.product.handler;

import by.cryptic.productservice.model.write.Product;
import by.cryptic.productservice.repository.write.ProductRepository;
import by.cryptic.productservice.service.command.product.ProductCreateCommand;
import by.cryptic.utils.CommandHandler;
import by.cryptic.utils.event.product.ProductCreatedEvent;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"products"})
public class ProductCreateCommandHandler implements CommandHandler<ProductCreateCommand> {

    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CacheManager cacheManager;

    @Transactional
    @Retry(name = "productRetry", fallbackMethod = "productCreateFallback")
    @Bulkhead(name = "productBulkhead", fallbackMethod = "productCreateFallback")
    public void handle(ProductCreateCommand productDTO) {
        Product product = Product.builder()
                .name(productDTO.name())
                .description(productDTO.description())
                .quantity(productDTO.quantity())
                .price(productDTO.price())
                .image(productDTO.image())
                .categoryId(productDTO.categoryId())
                .build();
        productRepository.save(product);

        eventPublisher.publishEvent(ProductCreatedEvent.builder()
                .productId(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .quantity(product.getQuantity())
                .price(product.getPrice())
                .image(product.getImage())
                .categoryId(product.getCategoryId())
                .createdBy(product.getCreatedBy())
                .build());
        Objects.requireNonNull(cacheManager.getCache("products"))
                .put("product:" + product.getId(), product);
    }

    public void productCreateFallback(ProductCreateCommand productCreateCommand, Throwable t) {
        log.error("Failed to create {}, {}", productCreateCommand.name(), t);
        throw new RuntimeException(t.getMessage());
    }
}
