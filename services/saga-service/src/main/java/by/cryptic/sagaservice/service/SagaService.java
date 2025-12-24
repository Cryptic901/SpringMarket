package by.cryptic.sagaservice.service;

import by.cryptic.utils.enums.OrderStatus;
import by.cryptic.utils.event.DomainEvent;
import by.cryptic.utils.event.cart.CartClearedBySagaEvent;
import by.cryptic.utils.event.cart.CartClearedFailedEvent;
import by.cryptic.utils.event.cart.CartClearedSuccessEvent;
import by.cryptic.utils.event.inventory.StockCreatedEvent;
import by.cryptic.utils.event.inventory.StockReservationFailedEvent;
import by.cryptic.utils.event.inventory.StockReservedEvent;
import by.cryptic.utils.event.order.FinalizeOrderEvent;
import by.cryptic.utils.event.order.OrderCreatedEvent;
import by.cryptic.utils.event.order.OrderFailedEvent;
import by.cryptic.utils.event.payment.PaymentCreatedEvent;
import by.cryptic.utils.event.payment.PaymentFailedEvent;
import by.cryptic.utils.event.payment.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SagaService {

    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;

    @KafkaListener(topics = {"order-topic", "payment-topic", "cart-topic", "inventory-topic"})
    public void sagaListener(DomainEvent event) {
        log.info("Event class in saga: {}", event.getClass().getSimpleName());
        switch (event) {
            case OrderCreatedEvent orderCreatedEvent ->
                    kafkaTemplate.send("saga-topic", orderCreatedEvent.getOrderId().toString(),
                            CartClearedBySagaEvent.builder()
                                    .listOfProducts(orderCreatedEvent.getListOfProducts())
                                    .userEmail(orderCreatedEvent.getUserEmail())
                                    .paymentMethod(orderCreatedEvent.getPaymentMethod())
                                    .orderId(orderCreatedEvent.getOrderId())
                                    .price(orderCreatedEvent.getPrice())
                                    .userId(orderCreatedEvent.getCreatedBy())
                                    .build());

            case CartClearedSuccessEvent cartClearedSuccessEvent ->
                    kafkaTemplate.send("saga-topic", cartClearedSuccessEvent.getOrderId().toString(),
                            StockCreatedEvent.builder()
                                    .listOfProducts(cartClearedSuccessEvent.getListOfProducts())
                                    .orderId(cartClearedSuccessEvent.getOrderId())
                                    .paymentMethod(cartClearedSuccessEvent.getPaymentMethod())
                                    .userEmail(cartClearedSuccessEvent.getUserEmail())
                                    .price(cartClearedSuccessEvent.getPrice())
                                    .createdBy(cartClearedSuccessEvent.getUserId())
                                    .lat(cartClearedSuccessEvent.getLat())
                                    .lon(cartClearedSuccessEvent.getLon())
                                    .warehouseLimit(cartClearedSuccessEvent.getWarehouseLimit())
                                    .build());

            case CartClearedFailedEvent cartClearedFailedEvent ->
                    kafkaTemplate.send("saga-topic", cartClearedFailedEvent.getOrderId().toString(),
                            OrderFailedEvent.builder()
                                    .orderId(cartClearedFailedEvent.getOrderId())
                                    .orderStatus(OrderStatus.FAILED)
                                    .userEmail(cartClearedFailedEvent.getUserEmail())
                                    .failureReason("Cart clearing failed")
                                    .build());

            case StockReservationFailedEvent stockReservationFailedEvent ->
                    kafkaTemplate.send("saga-topic", stockReservationFailedEvent.getOrderId().toString(),
                            OrderFailedEvent.builder()
                                    .orderStatus(OrderStatus.FAILED)
                                    .orderId(stockReservationFailedEvent.getOrderId())
                                    .userEmail(stockReservationFailedEvent.getUserEmail())
                                    .failureReason("Stock reservation failed")
                                    .build());

            case StockReservedEvent stockReservedEvent ->
                    kafkaTemplate.send("saga-topic", stockReservedEvent.getOrderId().toString(),
                            PaymentCreatedEvent.builder()
                                    .paymentId(UUID.randomUUID())
                                    .paymentMethod(stockReservedEvent.getPaymentMethod())
                                    .userEmail(stockReservedEvent.getUserEmail())
                                    .orderId(stockReservedEvent.getOrderId())
                                    .price(stockReservedEvent.getOrderPrice())
                                    .userId(stockReservedEvent.getUserId())
                                    .build());

            case PaymentSuccessEvent paymentSuccessEvent ->
                    kafkaTemplate.send("saga-topic", paymentSuccessEvent.getOrderId().toString(),
                            FinalizeOrderEvent.builder()
                                    .orderId(paymentSuccessEvent.getOrderId())
                                    .paymentId(paymentSuccessEvent.getPaymentId())
                                    .orderStatus(OrderStatus.IN_PROGRESS_OF_DELIVERY)
                                    .build());

            case PaymentFailedEvent paymentFailedEvent ->
                    kafkaTemplate.send("saga-topic", paymentFailedEvent.getOrderId().toString(),
                            OrderFailedEvent.builder()
                                    .orderId(paymentFailedEvent.getOrderId())
                                    .orderStatus(OrderStatus.FAILED)
                                    .userEmail(paymentFailedEvent.getEmail())
                                    .failureReason("Payment failed")
                                    .build());

            default -> log.warn("Ignoring event {}", event.getClass().getSimpleName());
        }
    }
}