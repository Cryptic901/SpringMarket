package by.cryptic.sagaservice.service;

import by.cryptic.utils.OrderStatus;
import by.cryptic.utils.event.DomainEvent;
import by.cryptic.utils.event.inventory.StockCreatedEvent;
import by.cryptic.utils.event.inventory.StockReservationFailedEvent;
import by.cryptic.utils.event.inventory.StockReservedEvent;
import by.cryptic.utils.event.order.FinalizeOrderEvent;
import by.cryptic.utils.event.order.OrderCanceledEvent;
import by.cryptic.utils.event.order.OrderCreatedEvent;
import by.cryptic.utils.event.payment.PaymentCreatedEvent;
import by.cryptic.utils.event.payment.PaymentFailedEvent;
import by.cryptic.utils.event.payment.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SagaService {

    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;

    @KafkaListener(topics = {"order-topic", "payment-topic"})
    public void sagaListener(DomainEvent event) {
        switch (event) {
            case OrderCreatedEvent orderCreatedEvent -> {
                kafkaTemplate.send("saga-topic", orderCreatedEvent.getOrderId().toString(),
                        StockCreatedEvent.builder()
                                .listOfProducts(orderCreatedEvent.getListOfProducts())
                                .orderId(orderCreatedEvent.getOrderId())
                                .userEmail(orderCreatedEvent.getUserEmail())
                                .price(orderCreatedEvent.getPrice())
                                .createdBy(orderCreatedEvent.getCreatedBy())
                                .build());
            }

            case StockReservationFailedEvent stockReservationFailedEvent ->
                    kafkaTemplate.send("saga-topic", stockReservationFailedEvent.getOrderId().toString(),
                            OrderCanceledEvent.builder()
                                    .orderId(stockReservationFailedEvent.getOrderId())
                                    .orderStatus(OrderStatus.CANCELLED)
                                    .userEmail(stockReservationFailedEvent.getUserEmail())
                                    .cancelReason("Stock reservation failed")
                                    .build());

            case StockReservedEvent stockReservedEvent ->
                    kafkaTemplate.send("saga-topic", stockReservedEvent.getOrderId().toString(),
                            PaymentCreatedEvent.builder()
                                    .paymentId(UUID.randomUUID())
                                    .userEmail(stockReservedEvent.getUserEmail())
                                    .orderId(stockReservedEvent.getOrderId())
                                    .price(stockReservedEvent.getOrderPrice())
                                    .userId(stockReservedEvent.getUserId())
                                    .build());

            case PaymentSuccessEvent paymentSuccessEvent -> {
                kafkaTemplate.send("saga-topic", paymentSuccessEvent.getOrderId().toString(),
                        FinalizeOrderEvent.builder()
                                .orderId(paymentSuccessEvent.getOrderId())
                                .paymentId(paymentSuccessEvent.getPaymentId())
                                .build());
            }


            case PaymentFailedEvent paymentFailedEvent ->
                    kafkaTemplate.send("saga-topic", paymentFailedEvent.getOrderId().toString(),
                            OrderCanceledEvent.builder()
                                    .orderId(paymentFailedEvent.getOrderId())
                                    .orderStatus(OrderStatus.CANCELLED)
                                    .userEmail(paymentFailedEvent.getEmail())
                                    .cancelReason("Payment failed")
                                    .build());

            default -> throw new IllegalStateException("Unexpected event: " + event);
        }
    }
}