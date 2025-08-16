package by.cryptic.productservice.service.command.handler;

import by.cryptic.exceptions.CreatingException;
import by.cryptic.productservice.model.write.Product;
import by.cryptic.productservice.repository.write.ProductRepository;
import by.cryptic.productservice.service.command.ProductCreateCommand;
import by.cryptic.utils.CommandHandler;
import by.cryptic.utils.ProductStatus;
import by.cryptic.utils.event.product.ProductCreatedEvent;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCreateCommandHandler implements CommandHandler<ProductCreateCommand> {

    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CacheManager cacheManager;

    @Override
    @Transactional
    @Retry(name = "productRetry", fallbackMethod = "productCreateRetryFallback")
    public void handle(ProductCreateCommand productDTO) {
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

        eventPublisher.publishEvent(ProductCreatedEvent.builder()
                .productId(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .quantity(product.getQuantity())
                .price(product.getPrice())
                .image(product.getImage())
                .categoryId(product.getCategoryId())
                .createdBy(product.getCreatedBy())
                .productStatus(product.getProductStatus())
                .build());
        Objects.requireNonNull(cacheManager.getCache("products"))
                .put("product:" + product.getId(), product);
    }

    public void productCreateRetryFallback(ProductCreateCommand productCreateCommand, Throwable t) {
        log.error("Failed to create {} after all retry attempts. Cause: {}", productCreateCommand.name(), t.getMessage(), t);
        throw new CreatingException("Failed to create review:" + productCreateCommand.name(), t);
    }
}
