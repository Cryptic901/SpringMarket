package by.cryptic.cartservice.service.command.handler;

import by.cryptic.cartservice.client.ProductServiceClient;
import by.cryptic.exceptions.NotEnoughProductsException;
import by.cryptic.cartservice.model.write.Cart;
import by.cryptic.cartservice.model.write.CartProduct;
import by.cryptic.cartservice.publisher.CartEventPublisher;
import by.cryptic.cartservice.repository.write.CartRepository;
import by.cryptic.cartservice.service.command.CartAddCommand;
import by.cryptic.cartservice.util.CartUtil;
import by.cryptic.exceptions.CreatingException;
import by.cryptic.utils.handler.CommandHandler;
import by.cryptic.utils.DTO.ProductDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartAddCommandHandler implements CommandHandler<CartAddCommand> {

    private final CartRepository cartRepository;
    private final CartUtil cartUtil;
    private final CacheManager cacheManager;
    private final ProductServiceClient productServiceClient;
    private final CartEventPublisher cartEventPublisher;

    @Override
    @Transactional
    public void handle(CartAddCommand command) {
        Cart cart = getOrCreateCart(command);

        ProductDTO product = getProductDTO(command);

        if (product == null) {
            throw new EntityNotFoundException("Product with id %s not found"
                    .formatted(command.productId()));
        }

        List<CartProduct> products = createOrAddProduct(command, cart, product);
        cart.setTotal(cartUtil.getTotalPrice(products));

        cartRepository.save(cart);

        cartEventPublisher.cartAddView(cart, product, command);
        updateCache(cart);
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
                throw new NotEnoughProductsException("You're trying to add product, that is out of stock");
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

    private void updateCache(Cart cart) {
        Objects.requireNonNull(cacheManager.getCache("carts"))
                .put("cart:" + cart.getUserId(), cart);
    }

    @CircuitBreaker(name = "productCircuitBreaker", fallbackMethod = "productClientCircuitBreakerFallback")
    public ProductDTO getProductDTO(CartAddCommand command) {
        return productServiceClient.getProductById(command.productId()).getBody();
    }

    @CircuitBreaker(name = "cartCircuitBreaker", fallbackMethod = "cartCreatingCircuitBreakerFallback")
    public Cart getOrCreateCart(CartAddCommand command) {
        return cartRepository.findByUserIdWithItems(command.userId())
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .userId(command.userId())
                            .total(BigDecimal.ZERO)
                            .items(new ArrayList<>())
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    public ProductDTO productClientCircuitBreakerFallback(CartAddCommand command, Throwable t) {
        log.error("Failed to add {} after all attempts to cart. Cause: {}", command.productId(), t.getMessage(), t);
        throw new CreatingException("Failed to add product:" + command.productId(), t);
    }

    public Cart cartCreatingCircuitBreakerFallback(CartAddCommand command, Throwable t) {
        log.error("Failed to create or find cart of user {} after all attempts. Cause: {}", command.userId(), t.getMessage(), t);
        throw new CreatingException("Failed to create cart:" + command.productId(), t);
    }
}
