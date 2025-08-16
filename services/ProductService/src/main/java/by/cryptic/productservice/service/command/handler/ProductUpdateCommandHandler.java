package by.cryptic.productservice.service.command.handler;

import by.cryptic.exceptions.UpdatingException;
import by.cryptic.productservice.mapper.ProductMapper;
import by.cryptic.productservice.model.write.Product;
import by.cryptic.productservice.repository.write.ProductRepository;
import by.cryptic.productservice.service.command.ProductUpdateCommand;
import by.cryptic.utils.CommandHandler;
import by.cryptic.utils.event.product.ProductUpdatedEvent;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductUpdateCommandHandler implements CommandHandler<ProductUpdateCommand> {

    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    @Retry(name = "productRetry", fallbackMethod = "productUpdateRetryFallback")
    @CachePut(cacheNames = "products", key = "'product:' + updateProductDTO.productId()")
    public void handle(ProductUpdateCommand updateProductDTO) {
        Product product = productRepository.findById(updateProductDTO.productId())
                .orElseThrow(() -> new EntityNotFoundException
                        ("Product not found with id : %s".formatted(updateProductDTO.productId())));
        if (!product.getCreatedBy().equals(updateProductDTO.createdBy())) {
            throw new IllegalCallerException("You cannot update product because it's not yours");
        }
        ProductMapper.updateEntity(product, updateProductDTO);
        productRepository.save(product);
        eventPublisher.publishEvent(ProductUpdatedEvent.builder()
                .productId(updateProductDTO.productId())
                .name(updateProductDTO.name())
                .image(updateProductDTO.image())
                .description(updateProductDTO.description())
                .quantity(updateProductDTO.quantity())
                .price(updateProductDTO.price())
                .categoryId(product.getCategoryId())
                .productStatus(updateProductDTO.productStatus())
                .build());
    }

    public void productUpdateRetryFallback(ProductUpdateCommand productUpdateCommand, Throwable t) {
        log.error("Failed to update {} after all retry attempts. Cause: {}", productUpdateCommand.name(), t.getMessage(), t);
        throw new UpdatingException("Failed to update review:" + productUpdateCommand.name(), t);
    }
}
