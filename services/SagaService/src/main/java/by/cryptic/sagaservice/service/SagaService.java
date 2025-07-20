package by.cryptic.sagaservice.service;

import by.cryptic.utils.OrderStatus;
import by.cryptic.utils.event.DomainEvent;
import by.cryptic.utils.event.order.OrderCanceledEvent;
import by.cryptic.utils.event.order.OrderCreatedEvent;
import by.cryptic.utils.event.order.OrderSuccessEvent;
import by.cryptic.utils.event.payment.PaymentCreatedEvent;
import by.cryptic.utils.event.payment.PaymentFailedEvent;
import by.cryptic.utils.event.payment.PaymentSuccessEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SagaService {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = {"order-topic", "payment-topic"})
    public void onOrderEvent(String rawJson) throws JsonProcessingException {
        DomainEvent event = objectMapper.readValue(rawJson, DomainEvent.class);
        switch (event) {
            case OrderSuccessEvent orderSuccessEvent -> kafkaTemplate.send("order-topic", objectMapper
                    .writeValueAsString(OrderCreatedEvent.builder()
                            .orderId(orderSuccessEvent.getOrderId())
                            .price(orderSuccessEvent.getPrice())
                            .orderStatus(orderSuccessEvent.getOrderStatus())
                            .createdBy(orderSuccessEvent.getCreatedBy())
                            .listOfProducts(orderSuccessEvent.getListOfProducts())
                            .build()));

            case PaymentSuccessEvent paymentSuccessEvent -> kafkaTemplate.send("payment-topic", objectMapper
                    .writeValueAsString(PaymentCreatedEvent.builder()
                            .paymentId(paymentSuccessEvent.getPaymentId())
                            .userId(paymentSuccessEvent.getUserId())
                            .paymentMethod(paymentSuccessEvent.getPaymentMethod())
                            .price(paymentSuccessEvent.getPrice())
                            .orderId(paymentSuccessEvent.getOrderId())
                            .paymentStatus(paymentSuccessEvent.getPaymentStatus())
                            .build()));

            case PaymentFailedEvent paymentFailedEvent -> kafkaTemplate.send("order-topic", objectMapper
                    .writeValueAsString(OrderCanceledEvent.builder()
                            .orderId(paymentFailedEvent.getOrderId())
                            .orderStatus(OrderStatus.CANCELLED)
                            .userEmail(paymentFailedEvent.getEmail())
                            .build()));

            default -> throw new IllegalStateException("Unexpected event: " + event);
        }
    }
}
