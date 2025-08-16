package by.cryptic.cartservice.service.command.handler;

import by.cryptic.cartservice.model.write.Cart;
import by.cryptic.cartservice.repository.write.CartRepository;
import by.cryptic.cartservice.service.command.CartClearCommand;
import by.cryptic.cartservice.util.CartUtil;
import by.cryptic.utils.CommandHandler;
import by.cryptic.utils.event.cart.CartClearedEvent;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"carts"})
public class CartClearCommandHandler implements CommandHandler<CartClearCommand> {

    private final CartRepository cartRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CartUtil cartUtil;

    @Override
    @Transactional
    @Retry(name = "cartRetry", fallbackMethod = "cartClearRetryFallback")
    @CacheEvict(cacheNames = "carts", key = "'cart:' + #command.userId()")
    public void handle(CartClearCommand command) {
        Cart cart = cartRepository.findByUserIdWithItems(command.userId())
                .orElseThrow(() -> new EntityNotFoundException("You don't have any products in your cart"));
        cartUtil.clearCart(cart);
        eventPublisher.publishEvent(CartClearedEvent.builder()
                .cartId(cart.getId())
                .build());
    }

    public void cartClearRetryFallback(CartClearCommand cartClearCommand, Throwable t) {
        log.error("Failed to clear cart {}, {}", cartClearCommand.userId(), t);
        throw new RuntimeException(t.getMessage());
    }
}
