package by.cryptic.cartservice.service.command.handler;

import by.cryptic.cartservice.util.CartUtil;
import by.cryptic.utils.event.cart.CartClearedEvent;
import by.cryptic.cartservice.model.write.Cart;
import by.cryptic.cartservice.repository.write.CartRepository;
import by.cryptic.cartservice.service.command.CartClearCommand;
import by.cryptic.utils.CommandHandler;
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

@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"carts"})
public class CartClearCommandHandler implements CommandHandler<CartClearCommand> {

    private final CartRepository cartRepository;
    private final CacheManager cacheManager;
    private final ApplicationEventPublisher eventPublisher;
    private final CartUtil cartUtil;

    @Transactional
    @Retry(name = "cartRetry", fallbackMethod = "cartClearFallback")
    @Bulkhead(name = "cartBulkhead", fallbackMethod = "cartClearFallback")
    public void handle(CartClearCommand command) {
        Cart cart = cartRepository.findByUserIdWithItems(command.userId())
                .orElseThrow(() -> new EntityNotFoundException("You don't have any products in your cart"));
        cartUtil.clearCart(cart);
        eventPublisher.publishEvent(CartClearedEvent.builder()
                .cartId(cart.getId())
                .build());
        Objects.requireNonNull(cacheManager.getCache("carts")).evict(command.userId());
    }

    public void cartClearFallback(CartClearCommand cartClearCommand, Throwable t) {
        log.error("Failed to clear cart {}, {}", cartClearCommand.userId(), t);
        throw new RuntimeException(t.getMessage());
    }
}
