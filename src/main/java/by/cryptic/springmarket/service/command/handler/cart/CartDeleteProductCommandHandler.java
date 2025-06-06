package by.cryptic.springmarket.service.command.handler.cart;

import by.cryptic.springmarket.event.cart.CartDeletedProductEvent;
import by.cryptic.springmarket.model.write.AppUser;
import by.cryptic.springmarket.model.write.CartProduct;
import by.cryptic.springmarket.repository.write.CartProductRepository;
import by.cryptic.springmarket.repository.write.CartRepository;
import by.cryptic.springmarket.service.command.CartDeleteProductCommand;
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

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"carts"})
public class CartDeleteProductCommandHandler implements CommandHandler<CartDeleteProductCommand> {

    private final AuthUtil authUtil;
    private final CacheManager cacheManager;
    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;
    private final CartUtil cartUtil;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void handle(CartDeleteProductCommand command) {
        AppUser user = authUtil.getUserFromContext();
        List<CartProduct> cart = cartRepository.findByUserIdWithItems(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("You don't have any products in your cart"))
                .getItems();
        Iterator<CartProduct> iterator = cart.iterator();
        while (iterator.hasNext()) {
            CartProduct cartProduct = iterator.next();
            if (cartProduct.getProduct().getId().equals(command.productId())) {
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
        Objects.requireNonNull(cacheManager.getCache("carts")).evict(authUtil.getUserFromContext());
        eventPublisher.publishEvent(new CartDeletedProductEvent(user.getCart().getId(), command.productId()));
    }
}
