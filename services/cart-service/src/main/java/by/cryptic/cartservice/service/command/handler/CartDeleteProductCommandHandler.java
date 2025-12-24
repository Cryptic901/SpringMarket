package by.cryptic.cartservice.service.command.handler;

import by.cryptic.cartservice.model.write.Cart;
import by.cryptic.cartservice.model.write.CartProduct;
import by.cryptic.cartservice.publisher.CartEventPublisher;
import by.cryptic.cartservice.repository.write.CartProductRepository;
import by.cryptic.cartservice.repository.write.CartRepository;
import by.cryptic.cartservice.service.command.CartDeleteProductCommand;
import by.cryptic.cartservice.util.CartUtil;
import by.cryptic.exceptions.DeletingException;
import by.cryptic.exceptions.EmptyCartException;
import by.cryptic.utils.handler.CommandHandler;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartDeleteProductCommandHandler implements CommandHandler<CartDeleteProductCommand> {

    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;
    private final CartUtil cartUtil;
    private final CacheManager cacheManager;
    private final CartEventPublisher cartEventPublisher;

    @Override
    @Transactional
    @Retry(name = "cartRetry", fallbackMethod = "cartDeleteProductRetryFallback")
    public void handle(CartDeleteProductCommand command) {
        Cart cart = cartRepository.findByUserIdWithItems(command.userId())
                .orElseThrow(() -> new EntityNotFoundException("You don't have any products in your cart"));
        List<CartProduct> cartProducts = cart.getItems();

        decreasingProducts(cartProducts, command);

        cart.setTotal(cartUtil.getTotalPrice(cartProducts));

        cartEventPublisher.deleteCartView(command);

        updateCache(cart);
    }

    private void updateCache(Cart cart) {
        try {
            Objects.requireNonNull(cacheManager.getCache("carts"))
                    .put("cart:" + cart.getUserId(), cart);
        } catch (Exception e) {
            log.warn("Failed to update cart cache {}", cart.getUserId(), e);
        }
    }

    public void decreasingProducts(List<CartProduct> cartProducts,
                                   CartDeleteProductCommand command) {
        if (cartProducts.isEmpty()) {
            throw new EmptyCartException("Your cart is empty");
        }
        Iterator<CartProduct> iterator = cartProducts.iterator();
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
            } else {
                throw new EntityNotFoundException("You don't have product with id "
                        + command.productId() + " in your cart");
            }
        }
    }

    public void cartDeleteProductRetryFallback(CartDeleteProductCommand command, Throwable t) {
        log.error("Failed to delete {} from cart after all retry attempts. Cause: {}", command.productId(), t.getMessage(), t);
        throw new DeletingException("Failed to delete from cart:" + command.productId(), t);
    }
}
