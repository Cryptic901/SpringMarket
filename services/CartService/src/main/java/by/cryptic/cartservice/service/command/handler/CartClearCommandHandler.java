package by.cryptic.cartservice.service.command.handler;

import by.cryptic.cartservice.util.CartUtil;
import by.cryptic.utils.event.cart.CartClearedEvent;
import by.cryptic.cartservice.model.write.Cart;
import by.cryptic.cartservice.repository.write.CartRepository;
import by.cryptic.cartservice.service.command.CartClearCommand;
import by.cryptic.utils.CommandHandler;
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
    public void handle(CartClearCommand command) {
        Cart cart = cartRepository.findByUserIdWithItems(command.userId())
                .orElseThrow(() -> new EntityNotFoundException("You don't have any products in your cart"));
        cartUtil.clearCart(cart);
        Objects.requireNonNull(cacheManager.getCache("carts")).evict(command.userId());
        eventPublisher.publishEvent(new CartClearedEvent(cart.getId()));
    }
}
