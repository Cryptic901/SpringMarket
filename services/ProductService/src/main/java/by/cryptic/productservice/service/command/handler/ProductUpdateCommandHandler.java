package by.cryptic.productservice.service.command.handler;

import by.cryptic.exceptions.UpdatingException;
import by.cryptic.productservice.mapper.ProductMapper;
import by.cryptic.productservice.model.write.Product;
import by.cryptic.productservice.publisher.ProductEventPublisher;
import by.cryptic.productservice.repository.write.ProductRepository;
import by.cryptic.productservice.service.command.ProductUpdateCommand;
import by.cryptic.utils.handler.CommandHandler;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductUpdateCommandHandler implements CommandHandler<ProductUpdateCommand> {

    private final ProductRepository productRepository;
    private final CacheManager cacheManager;
    private final ProductEventPublisher productEventPublisher;

    @Override
    @Transactional
    public void handle(ProductUpdateCommand updateProductDTO) {
        Product product = getProductAndValidateAccess(updateProductDTO);

        updateProduct(product, updateProductDTO);
        productEventPublisher.updateProductView(product, updateProductDTO);

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

    private Product getProductAndValidateAccess(ProductUpdateCommand updateProductDTO) {
        Product product = productRepository.findById(updateProductDTO.productId())
                .orElseThrow(() -> new EntityNotFoundException
                        ("Product not found with id : %s".formatted(updateProductDTO.productId())));
        if (!product.getCreatedBy().equals(updateProductDTO.createdBy())) {
            throw new IllegalCallerException("You cannot update product because it's not yours");
        }
        return product;
    }

    @Retry(name = "productRetry", fallbackMethod = "productUpdateRetryFallback")
    public void updateProduct(Product product, ProductUpdateCommand updateProductDTO) {
        ProductMapper.updateEntity(product, updateProductDTO);
        productRepository.save(product);
    }

    public void productUpdateRetryFallback(Product product, ProductUpdateCommand productUpdateCommand, Throwable t) {
        log.error("Failed to update {} after all retry attempts. Cause: {}", productUpdateCommand.name(), t.getMessage(), t);
        throw new UpdatingException("Failed to update review:" + productUpdateCommand.name(), t);
    }
}
