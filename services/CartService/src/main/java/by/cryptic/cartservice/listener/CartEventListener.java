package by.cryptic.cartservice.listener;

import by.cryptic.cartservice.model.read.CartProductView;
import by.cryptic.cartservice.model.read.CartView;
import by.cryptic.cartservice.model.write.Cart;
import by.cryptic.cartservice.publisher.CartEventPublisher;
import by.cryptic.cartservice.repository.read.CartViewRepository;
import by.cryptic.cartservice.repository.write.CartRepository;
import by.cryptic.cartservice.util.CartUtil;
import by.cryptic.utils.event.DomainEvent;
import by.cryptic.utils.event.cart.CartAddedItemEvent;
import by.cryptic.utils.event.cart.CartClearedBySagaEvent;
import by.cryptic.utils.event.cart.CartClearedByUserEvent;
import by.cryptic.utils.event.cart.CartDeletedProductEvent;
import by.cryptic.utils.event.user.UserCreatedEvent;
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
    private final CartViewRepository cartViewRepository;
    private final CartRepository cartRepository;
    private final CartUtil cartUtil;
    private final CartEventPublisher cartEventPublisher;

    @KafkaListener(topics = {"user-topic", "cart-topic"}, groupId = "cart-group")
    public void listenCart(DomainEvent event) {
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
                cartView.setTotal(cartUtil.getTotalViewPrice(cartView.getProducts()));
                cartViewRepository.save(cartView);
            }
            case CartClearedByUserEvent cartClearedEvent -> {
                CartView cart = cartViewRepository.findCartViewByUserId(cartClearedEvent.getUserId())
                        .orElseThrow(() -> new EntityNotFoundException
                                ("Cart not found to user with id %s".formatted(cartClearedEvent.getUserId())));
                cart.getProducts().clear();
                cart.setTotal(BigDecimal.ZERO);
                cartViewRepository.save(cart);
            }
            case CartDeletedProductEvent cartDeletedProductEvent -> {
                CartView cartView = cartViewRepository.findCartViewByUserId(cartDeletedProductEvent.getUserId())
                        .orElseThrow(() -> new EntityNotFoundException
                                ("Cart not found to user with id %s".formatted(cartDeletedProductEvent.getUserId())));
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
                            cartView.setTotal(cartUtil.getTotalViewPrice(cartView.getProducts()));
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

    @KafkaListener(topics = "saga-topic")
    public void listenSaga(DomainEvent event) {
        log.info("-------------------------------------------");
        log.info("SAGA LISTENING IN CART EVENT LISTENER");
        log.info("!!!!!Event class: {}", event.getClass().getSimpleName());
        switch (event) {
            case CartClearedBySagaEvent cartClearedEvent -> {
                try {
                    CartView cart = cartViewRepository.findCartViewByUserId(cartClearedEvent.getUserId())
                            .orElseThrow(() -> new EntityNotFoundException
                                    ("Cart not found to user with id %s".formatted(cartClearedEvent.getUserId())));
                    cart.getProducts().clear();
                    cart.setTotal(BigDecimal.ZERO);
                    cartViewRepository.save(cart);

                    cartEventPublisher.cartClearedSuccessEventPublisher(cartClearedEvent);
                    log.info("SUCCESS!: {}", cartClearedEvent);
                } catch (Exception e) {
                    log.warn("FAILED!: {}", cartClearedEvent);
                    cartEventPublisher.cartClearedFailedEventPublisher(cartClearedEvent);
                }
            }
            default -> log.warn("Ignoring event {}", event);
        }
    }
}
