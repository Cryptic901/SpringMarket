package by.cryptic.productservice.service.command.product.handler;

import by.cryptic.productservice.mapper.ProductMapper;
import by.cryptic.productservice.model.write.Product;
import by.cryptic.productservice.repository.write.ProductRepository;
import by.cryptic.productservice.service.command.product.ProductUpdateCommand;
import by.cryptic.utils.CommandHandler;
import by.cryptic.utils.event.product.ProductUpdatedEvent;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@CacheConfig(cacheNames = {"products"})
public class ProductUpdateCommandHandler implements CommandHandler<ProductUpdateCommand> {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final CacheManager cacheManager;

    @Transactional
    @Retry(name = "productRetry", fallbackMethod = "productUpdateFallback")
    @Bulkhead(name = "productBulkhead", fallbackMethod = "productUpdateFallback")
    public void handle(ProductUpdateCommand updateProductDTO) {
        Product product = productRepository.findById(updateProductDTO.productId())
                .orElseThrow(() -> new EntityNotFoundException
                        ("Product not found with id : %s".formatted(updateProductDTO.productId())));
        productMapper.updateEntity(product, updateProductDTO);
        productRepository.save(product);
        eventPublisher.publishEvent(ProductUpdatedEvent.builder()
                .productId(updateProductDTO.productId())
                .name(updateProductDTO.name())
                .image(updateProductDTO.image())
                .description(updateProductDTO.description())
                .quantity(updateProductDTO.quantity())
                .price(updateProductDTO.price())
                .categoryId(product.getCategoryId())
                .build());
        Objects.requireNonNull(cacheManager.getCache("products"))
                .put("product:" + product.getId(), product);
    }

    public void productUpdateFallback(ProductUpdateCommand productUpdateCommand, Throwable t) {
        log.error("Failed to update {}, {}", productUpdateCommand.name(), t);
        throw new RuntimeException(t.getMessage());
    }
}
