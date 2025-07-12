package by.cryptic.cartservice.service.command.handler;

import by.cryptic.cartservice.client.ProductServiceClient;
import by.cryptic.cartservice.exception.NotEnoughProducts;
import by.cryptic.cartservice.mapper.CartMapper;
import by.cryptic.cartservice.model.write.Cart;
import by.cryptic.cartservice.model.write.CartProduct;
import by.cryptic.cartservice.repository.write.CartRepository;
import by.cryptic.cartservice.service.command.CartAddCommand;
import by.cryptic.cartservice.util.CartUtil;
import by.cryptic.utils.CommandHandler;
import by.cryptic.utils.DTO.ProductDTO;
import by.cryptic.utils.event.cart.CartAddedItemEvent;
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
    private final ApplicationEventPublisher eventPublisher;
    private final CacheManager cacheManager;
    private final CartUtil cartUtil;
    private final ProductServiceClient productServiceClient;

    @Transactional
    public void handle(CartAddCommand command) {
        Cart cart = cartRepository.findByUserIdWithItems(command.userId())
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .userId(command.userId())
                            .total(BigDecimal.ZERO)
                            .items(new ArrayList<>())
                            .build();
                    return cartRepository.save(newCart);
                });
        log.info("Cart after mapping to entity: {}", cart);
        ProductDTO product = productServiceClient.getProductById(command.productId()).getBody();

        if (product == null) {
            throw new EntityNotFoundException("Product with id %s not found"
                    .formatted(command.productId()));
        }

        List<CartProduct> products = createOrAddProduct(command, cart, product);

        cart.setTotal(cartUtil.getTotalPrice(products));
        cart.setUserId(command.userId());

        cartRepository.save(cart);
        eventPublisher.publishEvent(CartAddedItemEvent.builder()
                .cartId(cart.getId())
                .productId(command.productId())
                .price(product.price())
                .userId(command.userId())
                .build());
        Objects.requireNonNull(cacheManager.getCache("carts"))
                .put("carts:" + command.userId(), CartMapper.toDto(cart));
    }

    private List<CartProduct> createOrAddProduct(CartAddCommand command, Cart cart, ProductDTO product) {
        List<CartProduct> products = cart.getItems();
        CartProduct cartProduct = products
                .stream()
                .filter(cp -> cp.getProductId()
                        .equals(command.productId()))
                .findFirst()
                .orElse(null);
        if (cartProduct != null) {
            if (cartProduct.getQuantity() >= product.quantity()) {
                throw new NotEnoughProducts("You're trying to add product, that is out of stock");
            }
            cartProduct.setQuantity(cartProduct.getQuantity() + 1);
        } else {
            products.add(CartProduct.builder()
                    .pricePerUnit(product.price())
                    .productId(command.productId())
                    .quantity(1)
                    .cart(cart)
                    .build());
        }
        return products;
    }
}
