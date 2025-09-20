package by.cryptic.orderservice.publisher;

import by.cryptic.exceptions.DeletingException;
import by.cryptic.orderservice.mapper.OrderMapper;
import by.cryptic.orderservice.model.write.CustomerOrder;
import by.cryptic.orderservice.model.write.OutboxEntity;
import by.cryptic.orderservice.repository.write.OutboxRepository;
import by.cryptic.orderservice.service.command.OrderCancelCommand;
import by.cryptic.orderservice.service.command.OrderCreateCommand;
import by.cryptic.utils.DTO.OrderedProductDTO;
import by.cryptic.utils.enums.OrderStatus;
import by.cryptic.utils.event.DomainEvent;
import by.cryptic.utils.event.EventType;
import by.cryptic.utils.event.cart.CartClearedBySagaEvent;
import by.cryptic.utils.event.order.OrderCanceledEvent;
import by.cryptic.utils.event.order.OrderCreatedEvent;
import by.cryptic.utils.event.order.OrderFailedEvent;
import by.cryptic.utils.event.order.OrderSuccessEvent;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {

    private final OutboxRepository outboxRepository;


    private void saveToOutbox(UUID aggregateId, String aggregateType, EventType eventType, DomainEvent event) {
        OutboxEntity outbox = OutboxEntity.builder()
                .aggregateId(aggregateId)
                .aggregateType(aggregateType)
                .eventType(String.valueOf(eventType))
                .payload(event)
                .build();
        outboxRepository.save(outbox);
    }

    public void sentCartClearedEvent(CartClearedBySagaEvent event) {
        saveToOutbox(event.getUserId(), "cart",
                EventType.CartClearedEvent, event);
    }

    public void sentOrderCreatedEvent(CustomerOrder order, OrderCreateCommand command,
                                      List<OrderedProductDTO> productsToUpdate) {
        saveToOutbox(order.getId(), "order",
                EventType.OrderCreatedEvent,
                OrderCreatedEvent.builder()
                        .orderId(order.getId())
                        .userEmail(command.userEmail())
                        .paymentMethod(command.paymentMethod())
                        .listOfProducts(productsToUpdate)
                        .createdBy(command.userId())
                        .location(order.getLocation())
                        .orderStatus(order.getOrderStatus())
                        .price(order.getPrice())
                        .build());
    }

    public void sentOrderFailedEventWithException(CustomerOrder order,
                                                  List<OrderedProductDTO> productsToUpdate,
                                                  Exception e) {
        log.error("Order failed {}", e.getMessage());
        sentOrderFailedEvent(order, productsToUpdate);
    }

    private void sentOrderFailedEvent(CustomerOrder order, List<OrderedProductDTO> productsToUpdate) {
        saveToOutbox(order.getId(), "order",
                EventType.OrderFailedEvent,
                OrderFailedEvent.builder()
                        .orderId(order.getId() == null ? null : order.getId())
                        .userEmail(order.getUserEmail())
                        .listOfProducts(productsToUpdate)
                        .orderStatus(order.getOrderStatus())
                        .build());
    }

    public void sentOrderSuccessEvent(CustomerOrder order) {
        log.error("Order Success {}", order);
        saveToOutbox(order.getId(), "order",
                EventType.OrderSuccessEvent,
                OrderSuccessEvent.builder()
                        .orderStatus(order.getOrderStatus())
                        .orderId(order.getId())
                        .createdBy(order.getCreatedBy())
                        .listOfProducts(order.getProducts().stream().map(OrderMapper::toOrderedDto).toList())
                        .location(order.getLocation())
                        .userEmail(order.getUserEmail())
                        .build());
    }

    @Retry(name = "orderRetry", fallbackMethod = "orderCancelRetryFallback")
    public void cancelOrderWithRetry(CustomerOrder order, OrderCancelCommand command) {
        OutboxEntity outbox = OutboxEntity.builder()
                .aggregateId(command.orderId())
                .aggregateType("order")
                .eventType(String.valueOf(EventType.OrderCanceledEvent))
                .payload(OrderCanceledEvent.builder()
                        .orderId(order.getId())
                        .orderStatus(OrderStatus.CANCELLED)
                        .userEmail(order.getUserEmail())
                        .cancelReason("User cancelling")
                        .price(order.getPrice())
                        .location(order.getLocation())
                        .build())
                .build();
        outboxRepository.save(outbox);
        log.info("Order canceled successfully");
    }

    public void orderCancelRetryFallback(CustomerOrder order, OrderCancelCommand orderCancelCommand, Throwable t) {
        log.error("Failed to cancel {} after all retry attempts. Cause: {}", orderCancelCommand.orderId(), t.getMessage(), t);
        throw new DeletingException("Failed to delete order:" + orderCancelCommand.orderId(), t);
    }
}
