package by.cryptic.cartservice.service.command.handler;

import by.cryptic.cartservice.util.CartUtil;
import by.cryptic.utils.event.cart.CartDeletedProductEvent;
import by.cryptic.cartservice.model.write.CartProduct;
import by.cryptic.cartservice.repository.write.CartProductRepository;
import by.cryptic.cartservice.repository.write.CartRepository;
import by.cryptic.cartservice.service.command.CartDeleteProductCommand;
import by.cryptic.utils.CommandHandler;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"carts"})
public class CartDeleteProductCommandHandler implements CommandHandler<CartDeleteProductCommand> {

    private final CacheManager cacheManager;
    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CartUtil cartUtil;

    @Transactional
    public void handle(CartDeleteProductCommand command) {
        List<CartProduct> cart = cartRepository.findByUserIdWithItems(command.userId())
                .orElseThrow(() -> new EntityNotFoundException("You don't have any products in your cart"))
                .getItems();
        Iterator<CartProduct> iterator = cart.iterator();
        while (iterator.hasNext()) {
            CartProduct cartProduct = iterator.next();
            if (cartProduct.getProductId().equals(command.productId())) {
                if (cartProduct.getQuantity() > 1) {
                    cartProduct.setQuantity(cartProduct.getQuantity() - 1);
                    cartProductRepository.save(cartProduct);
                } else {
                    iterator.remove();
                    cartProductRepository.delete(cartProduct);
                }
            }
        }
        cartUtil.getTotalPrice(cart);
        Objects.requireNonNull(cacheManager.getCache("carts")).evict(command.userId());
        eventPublisher.publishEvent(new CartDeletedProductEvent(cart.getFirst().getCart().getId(), command.productId()));
    }
}
