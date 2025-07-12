package by.cryptic.productservice.service.command.product.handler;

import by.cryptic.productservice.model.write.Product;
import by.cryptic.productservice.repository.write.ProductRepository;
import by.cryptic.productservice.service.command.product.ProductCreateCommand;
import by.cryptic.utils.CommandHandler;
import by.cryptic.utils.event.product.ProductCreatedEvent;
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
public class ProductCreateCommandHandler implements CommandHandler<ProductCreateCommand> {

    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CacheManager cacheManager;

    @Transactional
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
                .put("product:" + product.getDescription() + '-' + product.getName(), product);
    }
}
