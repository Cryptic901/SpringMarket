package by.cryptic.springmarket.service.command.handler.cart;

import by.cryptic.springmarket.event.cart.CartAddedItemEvent;
import by.cryptic.springmarket.model.write.AppUser;
import by.cryptic.springmarket.model.write.Cart;
import by.cryptic.springmarket.model.write.CartProduct;
import by.cryptic.springmarket.model.write.Product;
import by.cryptic.springmarket.repository.write.CartRepository;
import by.cryptic.springmarket.repository.write.ProductRepository;
import by.cryptic.springmarket.service.command.CartAddCommand;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"carts"})
public class CartAddCommandHandler implements CommandHandler<CartAddCommand> {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartUtil cartUtil;
    private final AuthUtil authUtil;
    private final ApplicationEventPublisher eventPublisher;
    private final CacheManager cacheManager;

    @Transactional
    public void handle(CartAddCommand command) {
        AppUser user = authUtil.getUserFromContext();
        Cart cart = cartRepository.findByUserIdWithItems(user.getId())
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .total(BigDecimal.ZERO)
                            .items(new ArrayList<>())
                            .build();
                    return cartRepository.save(newCart);
                });
        log.info("Cart after mapping to entity: {}", cart);
        List<CartProduct> products = createOrAddProduct(command, cart);

        cart.setTotal(cartUtil.getTotalPrice(products));
        cart.setUser(user);

        cartRepository.save(cart);
        eventPublisher.publishEvent(CartAddedItemEvent.builder()
                .cartId(cart.getId())
                .productId(command.productId()));
        Objects.requireNonNull(cacheManager.getCache("carts")).put(user.getId(), cart);
    }

    private List<CartProduct> createOrAddProduct(CartAddCommand command, Cart cart) {
        Product product = productRepository.findById(command.productId())
                .orElseThrow(() -> new EntityNotFoundException("You're trying to add product that doesn't exists"));
        List<CartProduct> products = cart.getItems();
        CartProduct cartProduct = products
                .stream()
                .filter(cp -> cp.getProduct().getId()
                        .equals(command.productId()))
                .findFirst()
                .orElse(null);
        if (cartProduct != null) {
            cartProduct.setQuantity(cartProduct.getQuantity() + 1);
        } else {
            products.add(CartProduct.builder()
                    .pricePerUnit(product.getPrice())
                    .product(product)
                    .quantity(1)
                    .cart(cart)
                    .build());
        }
        return products;
    }
}
