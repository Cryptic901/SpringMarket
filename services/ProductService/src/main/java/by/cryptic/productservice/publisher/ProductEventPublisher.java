package by.cryptic.productservice.publisher;

import by.cryptic.exceptions.CreatingException;
import by.cryptic.exceptions.DeletingException;
import by.cryptic.exceptions.UpdatingException;
import by.cryptic.productservice.model.write.OutboxEntity;
import by.cryptic.productservice.model.write.Product;
import by.cryptic.productservice.repository.write.OutboxRepository;
import by.cryptic.productservice.service.command.ProductUpdateCommand;
import by.cryptic.utils.event.EventType;
import by.cryptic.utils.event.product.ProductCreatedEvent;
import by.cryptic.utils.event.product.ProductDeletedEvent;
import by.cryptic.utils.event.product.ProductUpdatedEvent;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductEventPublisher {

    private final OutboxRepository outboxRepository;

    @Retry(name = "productRetry", fallbackMethod = "productCreateRetryFallback")
    public void saveProductView(Product product) {
        OutboxEntity outboxEntity = OutboxEntity.builder()
                .aggregateId(product.getId())
                .aggregateType("product")
                .eventType(String.valueOf(EventType.ProductCreatedEvent))
                .payload(ProductCreatedEvent.builder()
                        .productId(product.getId())
                        .name(product.getName())
                        .description(product.getDescription())
                        .quantity(product.getQuantity())
                        .price(product.getPrice())
                        .image(product.getImage())
                        .categoryId(product.getCategoryId())
                        .createdBy(product.getCreatedBy())
                        .productStatus(product.getProductStatus())
                        .build())
                .build();
        outboxRepository.save(outboxEntity);
    }

    @Retry(name = "productRetry", fallbackMethod = "productDeleteRetryFallback")
    public void deleteProductAndView(Product product) {
        OutboxEntity outboxEntity = OutboxEntity.builder()
                .aggregateId(product.getId())
                .aggregateType("product")
                .eventType(String.valueOf(EventType.ProductDeletedEvent))
                .payload(ProductDeletedEvent.builder()
                        .productId(product.getId())
                        .build())
                .build();
        outboxRepository.save(outboxEntity);
    }

    @Retry(name = "productRetry", fallbackMethod = "productUpdateRetryFallback")
    public void updateProductView(Product product, ProductUpdateCommand updateProductDTO) {
        OutboxEntity outboxEntity = OutboxEntity.builder()
                .aggregateId(product.getId())
                .aggregateType("product")
                .eventType(String.valueOf(EventType.ProductUpdatedEvent))
                .payload(ProductUpdatedEvent.builder()
                        .productId(updateProductDTO.productId())
                        .name(updateProductDTO.name())
                        .image(updateProductDTO.image())
                        .description(updateProductDTO.description())
                        .quantity(updateProductDTO.quantity())
                        .price(updateProductDTO.price())
                        .categoryId(product.getCategoryId())
                        .productStatus(updateProductDTO.productStatus())
                        .build())
                .build();
        outboxRepository.save(outboxEntity);
    }

    public void productCreateRetryFallback(Product product, Throwable t) {
        log.error("Failed to create {} after all retry attempts. Cause: {}", product.getName(), t.getMessage(), t);
        throw new CreatingException("Failed to create review:" + product.getName(), t);
    }

    public void productDeleteRetryFallback(Product product, Throwable t) {
        log.error("Failed to delete {} after all retry attempts. Cause: {}", product.getName(), t.getMessage(), t);
        throw new DeletingException("Failed to delete review:" + product.getName(), t);
    }

    public void productUpdateRetryFallback(Product product,ProductUpdateCommand productUpdateCommand, Throwable t) {
        log.error("Failed to update {} after all retry attempts. Cause: {}", productUpdateCommand.name(), t.getMessage(), t);
        throw new UpdatingException("Failed to update review:" + productUpdateCommand.name(), t);
    }
}
