package by.cryptic.productservice.service.command.handler;

import by.cryptic.exceptions.DeletingException;
import by.cryptic.productservice.model.write.Product;
import by.cryptic.productservice.repository.write.ProductRepository;
import by.cryptic.productservice.service.command.ProductDeleteCommand;
import by.cryptic.utils.CommandHandler;
import by.cryptic.utils.event.product.ProductDeletedEvent;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductDeleteCommandHandler implements CommandHandler<ProductDeleteCommand> {

    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    @CacheEvict(cacheNames = "products", key = "'product:' + #command.productId()")
    @Retry(name = "productRetry", fallbackMethod = "productDeleteRetryFallback")
    public void handle(ProductDeleteCommand command) {
        Product product = productRepository.findById(command.productId())
                .orElseThrow(() -> new EntityNotFoundException("Product with id %s not found"
                        .formatted(command.productId())));
        if (!product.getCreatedBy().equals(command.userId())) {
            throw new IllegalCallerException("You cannot delete product because it's not yours");
        }
        productRepository.deleteById(command.productId());
        eventPublisher.publishEvent(new ProductDeletedEvent(command.productId()));
    }

    public void productDeleteRetryFallback(ProductDeleteCommand productDeleteCommand, Throwable t) {
        log.error("Failed to delete {} after all retry attempts. Cause: {}", productDeleteCommand.productId(), t.getMessage(), t);
        throw new DeletingException("Failed to delete review:" + productDeleteCommand.productId(), t);
    }
}
