package by.cryptic.springmarket.service.command.handler.product;

import by.cryptic.springmarket.event.product.ProductDeletedEvent;
import by.cryptic.springmarket.repository.write.ProductRepository;
import by.cryptic.springmarket.service.command.ProductDeleteCommand;
import by.cryptic.springmarket.service.command.handler.CommandHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"products"})
public class ProductDeleteCommandHandler implements CommandHandler<ProductDeleteCommand> {

    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @CacheEvict(key = "'product:' + #command.productId()")
    public void handle(ProductDeleteCommand command) {
        productRepository.deleteById(command.productId());
        eventPublisher.publishEvent(new ProductDeletedEvent(command.productId()));
    }
}
