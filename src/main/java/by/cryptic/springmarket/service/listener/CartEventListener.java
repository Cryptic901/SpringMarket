package by.cryptic.springmarket.service.listener;

import by.cryptic.springmarket.enums.EventType;
import by.cryptic.springmarket.event.cart.CartAddedItemEvent;
import by.cryptic.springmarket.event.cart.CartClearedEvent;
import by.cryptic.springmarket.event.cart.CartDeletedProductEvent;
import by.cryptic.springmarket.model.read.CartProductView;
import by.cryptic.springmarket.model.read.CartView;
import by.cryptic.springmarket.repository.read.CartViewRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
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

    @KafkaListener(topics = "cart-topic", groupId = "cart-group")
    public void listenCart(String rawEvent) throws JsonProcessingException {
        JsonNode node = objectMapper.readTree(rawEvent);
        String type = node.get("eventType").asText();
        log.info("Received event type {}", type);
        switch (EventType.valueOf(type)) {
            case CartAddedItemEvent -> {
                CartAddedItemEvent event = objectMapper.treeToValue(node, CartAddedItemEvent.class);
                CartView cartView = cartViewRepository.findById(event.getCartId())
                        .orElse(CartView.builder()
                                .total(BigDecimal.ZERO)
                                .cartId(event.getCartId())
                                .products(new ArrayList<>())
                                .build());

                CartProductView newProduct = CartProductView.builder()
                        .productId(event.getProductId())
                        .price(event.getPrice())
                        .quantity(1)
                        .build();

                cartView.getProducts().stream()
                        .filter(p -> p.getProductId().equals(event.getProductId()))
                        .findFirst()
                        .ifPresentOrElse(
                                p -> p.setQuantity(p.getQuantity() + 1),
                                () -> cartView.getProducts().add(newProduct));
                cartView.setTotal(cartView.getTotal().add(event.getPrice()));
                cartViewRepository.save(cartView);
            }
            case CartClearedEvent -> {
                CartClearedEvent event = objectMapper.treeToValue(node, CartClearedEvent.class);
                CartView cart = cartViewRepository.findById(event.getCartId())
                        .orElseThrow(() -> new EntityNotFoundException
                                ("Cart not found with id %s".formatted(event.getCartId())));
                cart.getProducts().clear();
                cart.setTotal(BigDecimal.ZERO);
                cartViewRepository.save(cart);
            }
            case CartDeletedProductEvent -> {
                CartDeletedProductEvent event = objectMapper.treeToValue(node, CartDeletedProductEvent.class);
                CartView cartView = cartViewRepository.findById(event.getCartId())
                        .orElseThrow(() -> new EntityNotFoundException
                                ("Cart not found with id %s".formatted(event.getCartId())));
                cartView.getProducts().stream()
                        .filter(p -> p.getProductId().equals(event.getProductId()))
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
            default -> throw new IllegalStateException("Unexpected event type: " + type);
        }
    }
}

