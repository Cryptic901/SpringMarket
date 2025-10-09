package by.cryptic.cartservice.publisher;

import by.cryptic.cartservice.model.write.Cart;
import by.cryptic.cartservice.model.write.OutboxEntity;
import by.cryptic.cartservice.repository.write.OutboxRepository;
import by.cryptic.cartservice.service.command.CartAddCommand;
import by.cryptic.cartservice.service.command.CartDeleteProductCommand;
import by.cryptic.cartservice.util.CartUtil;
import by.cryptic.utils.DTO.ProductDTO;
import by.cryptic.utils.event.EventType;
import by.cryptic.utils.event.cart.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartEventPublisher {

    private final OutboxRepository outboxRepository;
    private final CartUtil cartUtil;

    public void cartAddView(Cart cart, ProductDTO product, CartAddCommand command) {
        OutboxEntity outbox = OutboxEntity.builder()
                .aggregateId(cart.getId())
                .aggregateType("cart")
                .eventType(EventType.CartAddedItemEvent.name())
                .payload(CartAddedItemEvent.builder()
                        .cartId(cart.getId())
                        .productId(command.productId())
                        .price(product.price())
                        .userId(command.userId())
                        .build())
                .build();
        outboxRepository.save(outbox);
    }

    public void clearCartAndCartView(Cart cart) {
        OutboxEntity outbox = OutboxEntity.builder()
                .aggregateId(cart.getUserId())
                .aggregateType("cart")
                .eventType(String.valueOf(EventType.CartClearedEvent))
                .payload(CartClearedByUserEvent.builder()
                        .userId(cart.getUserId())
                        .build())
                .build();
        outboxRepository.save(outbox);
        cartUtil.clearCart(cart);
    }

    public void deleteCartView(CartDeleteProductCommand command) {
        OutboxEntity outbox = OutboxEntity.builder()
                .aggregateId(command.userId())
                .aggregateType("cart")
                .eventType(String.valueOf(EventType.CartDeletedProductEvent))
                .payload(new CartDeletedProductEvent(command.userId(),
                        command.productId()))
                .build();
        outboxRepository.save(outbox);
    }

    public void cartClearedSuccessEventPublisher(CartClearedBySagaEvent cartClearedEvent) {
        OutboxEntity outbox = OutboxEntity.builder()
                .aggregateId(cartClearedEvent.getUserId())
                .aggregateType("cart")
                .eventType(String.valueOf(EventType.CartClearedSuccessEvent))
                .payload(CartClearedSuccessEvent.builder()
                        .listOfProducts(cartClearedEvent.getListOfProducts())
                        .userId(cartClearedEvent.getUserId())
                        .paymentMethod(cartClearedEvent.getPaymentMethod())
                        .price(cartClearedEvent.getPrice())
                        .orderId(cartClearedEvent.getOrderId())
                        .userEmail(cartClearedEvent.getUserEmail())
                        .build())
                .build();
        outboxRepository.save(outbox);
    }

    public void cartClearedFailedEventPublisher(CartClearedBySagaEvent cartClearedEvent) {
        OutboxEntity outbox = OutboxEntity.builder()
                .aggregateId(cartClearedEvent.getUserId())
                .aggregateType("cart")
                .eventType(String.valueOf(EventType.CartClearedFailedEvent))
                .payload(CartClearedFailedEvent.builder()
                        .userId(cartClearedEvent.getUserId())
                        .orderId(cartClearedEvent.getOrderId())
                        .userEmail(cartClearedEvent.getUserEmail())
                        .build())
                .build();
        outboxRepository.save(outbox);
    }
}
