package by.cryptic.cartservice.publisher;

import by.cryptic.cartservice.model.write.Cart;
import by.cryptic.cartservice.model.write.OutboxEntity;
import by.cryptic.cartservice.repository.write.OutboxRepository;
import by.cryptic.cartservice.service.command.CartAddCommand;
import by.cryptic.cartservice.service.command.CartDeleteProductCommand;
import by.cryptic.cartservice.util.CartUtil;
import by.cryptic.exceptions.CreatingException;
import by.cryptic.exceptions.DeletingException;
import by.cryptic.utils.DTO.ProductDTO;
import by.cryptic.utils.event.EventType;
import by.cryptic.utils.event.cart.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartEventPublisher {

    private final OutboxRepository outboxRepository;
    private final CartUtil cartUtil;

    @CircuitBreaker(name = "cartCircuitBreaker", fallbackMethod = "cartAddCircuitBreakerFallback")
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

    @Retry(name = "cartRetry", fallbackMethod = "cartClearRetryFallback")
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

    @Retry(name = "cartRetry", fallbackMethod = "cartDeleteProductRetryFallback")
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

    public void cartAddCircuitBreakerFallback(Cart cart, ProductDTO product, CartAddCommand command, Throwable t) {
        log.error("Failed to add {} after all attempts to cart. Cause: {}", product.name(), t.getMessage(), t);
        throw new CreatingException("Failed to add product:" + product.name(), t);
    }

    public void cartClearRetryFallback(Cart cart, Throwable t) {
        log.error("Failed to clear {} after all retry attempts. Cause: {}", cart.getId(), t.getMessage(), t);
        throw new DeletingException("Failed to clear cart:" + cart.getId(), t);
    }

    public void cartDeleteProductRetryFallback(CartDeleteProductCommand cartDeleteProductCommand, Throwable t) {
        log.error("Failed to delete from cart {} after all retry attempts. Cause: {}", cartDeleteProductCommand.productId(), t.getMessage(), t);
        throw new DeletingException("Failed to delete product from cart:" + cartDeleteProductCommand.productId(), t);
    }
}
