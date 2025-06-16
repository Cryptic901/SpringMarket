package by.cryptic.springmarket.service.command.handler.product;

import by.cryptic.springmarket.event.product.ProductUpdatedEvent;
import by.cryptic.springmarket.mapper.ProductMapper;
import by.cryptic.springmarket.model.write.Product;
import by.cryptic.springmarket.repository.write.ProductRepository;
import by.cryptic.springmarket.service.command.ProductUpdateCommand;
import by.cryptic.springmarket.service.command.handler.CommandHandler;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"products"})
public class ProductUpdateCommandHandler implements CommandHandler<ProductUpdateCommand> {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final CacheManager cacheManager;

    @Transactional
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
                .category(product.getCategory())
                .build());
        Objects.requireNonNull(cacheManager.getCache("products"))
                .put("product:" + product.getDescription() + '-' + product.getName(), product);
    }
}
