package by.cryptic.springmarket.service.command.handler.cart;

import by.cryptic.springmarket.event.cart.CartClearedEvent;
import by.cryptic.springmarket.model.write.AppUser;
import by.cryptic.springmarket.model.write.Cart;
import by.cryptic.springmarket.repository.write.CartRepository;
import by.cryptic.springmarket.service.command.CartClearCommand;
import by.cryptic.springmarket.service.command.handler.CommandHandler;
import by.cryptic.springmarket.util.AuthUtil;
import by.cryptic.springmarket.util.CartUtil;
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


    private final AuthUtil authUtil;
    private final CartRepository cartRepository;
    private final CacheManager cacheManager;
    private final ApplicationEventPublisher eventPublisher;
    private final CartUtil cartUtil;

    @Transactional
    public void handle(CartClearCommand command) {
        AppUser user = authUtil.getUserFromContext();
        Cart cart = cartRepository.findByUserIdWithItems(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("You don't have any products in your cart"));
        cartUtil.clearCart(cart);
        Objects.requireNonNull(cacheManager.getCache("carts")).evict(user.getId());
        eventPublisher.publishEvent(new CartClearedEvent(cart.getId()));
    }
}
