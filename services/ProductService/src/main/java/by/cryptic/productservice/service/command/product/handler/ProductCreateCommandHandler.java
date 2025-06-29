package by.cryptic.productservice.service.command.product.handler;

import by.cryptic.utils.event.product.ProductCreatedEvent;
import by.cryptic.productservice.mapper.CategoryMapper;
import by.cryptic.productservice.model.write.Category;
import by.cryptic.productservice.model.write.Product;
import by.cryptic.productservice.repository.write.CategoryRepository;
import by.cryptic.productservice.repository.write.ProductRepository;
import by.cryptic.productservice.service.command.product.ProductCreateCommand;
import by.cryptic.utils.CommandHandler;
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
public class ProductCreateCommandHandler implements CommandHandler<ProductCreateCommand> {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CacheManager cacheManager;
    private final CategoryMapper categoryMapper;

    @Transactional
    public void handle(ProductCreateCommand productDTO) {
        Category category = categoryRepository.findById(productDTO.categoryId())
                .orElseThrow(() -> new EntityNotFoundException
                        ("Category not found with id %s".formatted(productDTO.categoryId())));
        Product product = Product.builder()
                .name(productDTO.name())
                .description(productDTO.description())
                .quantity(productDTO.quantity())
                .price(productDTO.price())
                .image(productDTO.image())
                .category(category)
                .build();
        productRepository.save(product);
        eventPublisher.publishEvent(ProductCreatedEvent.builder()
                .productId(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .quantity(product.getQuantity())
                .price(product.getPrice())
                .image(product.getImage())
                .category(categoryMapper.toShortDto(category))
                .createdBy(product.getCreatedBy())
                .build());
        Objects.requireNonNull(cacheManager.getCache("products"))
                .put("product:" + product.getDescription() + '-' + product.getName(), product);
    }
}
