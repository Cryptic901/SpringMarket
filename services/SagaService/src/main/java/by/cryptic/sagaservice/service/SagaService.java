package by.cryptic.sagaservice.service;

import by.cryptic.utils.OrderStatus;
import by.cryptic.utils.event.DomainEvent;
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
            case OrderCreatedEvent orderCreatedEvent -> kafkaTemplate.send("saga-topic",
                    PaymentCreatedEvent.builder()
                            .paymentId(UUID.randomUUID())
                            .userEmail(orderCreatedEvent.getUserEmail())
                            .orderId(orderCreatedEvent.getOrderId())
                            .price(orderCreatedEvent.getPrice())
                            .userId(orderCreatedEvent.getCreatedBy())
                            .build());

            case PaymentSuccessEvent paymentSuccessEvent ->
                    kafkaTemplate.send("saga-topic", FinalizeOrderEvent.builder()
                            .orderId(paymentSuccessEvent.getOrderId())
                            .paymentId(paymentSuccessEvent.getPaymentId())
                            .build());


            case PaymentFailedEvent paymentFailedEvent -> kafkaTemplate.send("saga-topic",
                    OrderCanceledEvent.builder()
                            .orderId(paymentFailedEvent.getOrderId())
                            .orderStatus(OrderStatus.CANCELLED)
                            .userEmail(paymentFailedEvent.getEmail())
                            .build());

            default -> throw new IllegalStateException("Unexpected event: " + event);
        }
    }
}