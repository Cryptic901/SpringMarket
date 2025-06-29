package by.cryptic.productservice.service.command.product.handler;

import by.cryptic.utils.event.product.ProductDeletedEvent;
import by.cryptic.productservice.repository.write.ProductRepository;
import by.cryptic.productservice.service.command.product.ProductDeleteCommand;
import by.cryptic.utils.CommandHandler;
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
