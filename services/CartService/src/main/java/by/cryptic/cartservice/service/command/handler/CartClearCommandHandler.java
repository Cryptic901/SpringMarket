package by.cryptic.cartservice.service.command.handler;

import by.cryptic.cartservice.model.write.Cart;
import by.cryptic.cartservice.publisher.CartEventPublisher;
import by.cryptic.cartservice.repository.write.CartRepository;
import by.cryptic.cartservice.service.command.CartClearCommand;
import by.cryptic.utils.handler.CommandHandler;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"carts"})
public class CartClearCommandHandler implements CommandHandler<CartClearCommand> {

    private final CartRepository cartRepository;
    private final CartEventPublisher cartEventPublisher;

    @Override
    @Transactional
    @CacheEvict(cacheNames = "carts", key = "'cart:' + #command.userId()")
    public void handle(CartClearCommand command) {
        Cart cart = cartRepository.findByUserIdWithItems(command.userId())
                .orElseThrow(() -> new EntityNotFoundException("You don't have any products in your cart"));
        cartEventPublisher.clearCartAndCartView(cart);
    }
}
