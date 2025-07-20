package by.cryptic.cartservice.listener;

import by.cryptic.cartservice.model.read.CartProductView;
import by.cryptic.cartservice.model.read.CartView;
import by.cryptic.cartservice.model.write.Cart;
import by.cryptic.cartservice.repository.read.CartViewRepository;
import by.cryptic.cartservice.repository.write.CartRepository;
import by.cryptic.utils.event.DomainEvent;
import by.cryptic.utils.event.cart.CartAddedItemEvent;
import by.cryptic.utils.event.cart.CartClearedEvent;
import by.cryptic.utils.event.cart.CartDeletedProductEvent;
import by.cryptic.utils.event.user.UserCreatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;

@RequiredArgsConstructor
@Service
@Slf4j
public class CartEventListener {
    private final ObjectMapper objectMapper;
    private final CartViewRepository cartViewRepository;
    private final CartRepository cartRepository;

    @KafkaListener(topics = {"cart-topic", "user-topic"}, groupId = "cart-group")
    public void listenCart(String rawEvent) throws JsonProcessingException {
        DomainEvent event = objectMapper.readValue(rawEvent, DomainEvent.class);
        log.info("Received event type {}", event.getEventType());
        switch (event) {
            case CartAddedItemEvent cartAddedItemEvent -> {
                CartView cartView = cartViewRepository.findById(cartAddedItemEvent.getCartId())
                        .orElse(CartView.builder()
                                .total(BigDecimal.ZERO)
                                .cartId(cartAddedItemEvent.getCartId())
                                .products(new ArrayList<>())
                                .userId(cartAddedItemEvent.getUserId())
                                .build());

                CartProductView newProduct = CartProductView.builder()
                        .productId(cartAddedItemEvent.getProductId())
                        .price(cartAddedItemEvent.getPrice())
                        .quantity(1)
                        .build();

                cartView.getProducts().stream()
                        .filter(p -> p.getProductId()
                                .equals(cartAddedItemEvent.getProductId()))
                        .findFirst()
                        .ifPresentOrElse(
                                p -> p.setQuantity(p.getQuantity() + 1),
                                () -> cartView.getProducts().add(newProduct));
                cartView.setTotal(cartView.getTotal().add(cartAddedItemEvent.getPrice()));
                cartViewRepository.save(cartView);
            }
            case CartClearedEvent cartClearedEvent -> {
                CartView cart = cartViewRepository.findById(cartClearedEvent.getCartId())
                        .orElseThrow(() -> new EntityNotFoundException
                                ("Cart not found with id %s".formatted(cartClearedEvent.getCartId())));
                cart.getProducts().clear();
                cart.setTotal(BigDecimal.ZERO);
                cartViewRepository.save(cart);
            }
            case CartDeletedProductEvent cartDeletedProductEvent -> {
                CartView cartView = cartViewRepository.findById(cartDeletedProductEvent.getCartId())
                        .orElseThrow(() -> new EntityNotFoundException
                                ("Cart not found with id %s".formatted(cartDeletedProductEvent.getCartId())));
                cartView.getProducts().stream()
                        .filter(p -> p.getProductId().equals(cartDeletedProductEvent.getProductId()))
                        .findFirst()
                        .ifPresent(product -> {
                            int quantity = product.getQuantity() - 1;
                            if (quantity > 0) {
                                product.setQuantity(product.getQuantity() - 1);
                            } else {
                                cartView.getProducts().remove(product);
                            }
                            cartViewRepository.save(cartView);
                        });
            }
            case UserCreatedEvent userCreatedEvent -> {
                Cart cart = Cart.builder()
                        .userId(userCreatedEvent.getUserId())
                        .build();
                cartRepository.save(cart);
                CartView cartView = CartView.builder()
                        .cartId(cart.getId())
                        .userId(userCreatedEvent.getUserId())
                        .build();
                cartViewRepository.save(cartView);
            }
            default -> throw new IllegalStateException("Unexpected event type: " + event);
        }
    }
}

