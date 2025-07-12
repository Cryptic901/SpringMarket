package by.cryptic.sagaservice.service;

import by.cryptic.utils.OrderStatus;
import by.cryptic.utils.event.EventType;
import by.cryptic.utils.event.order.OrderCanceledEvent;
import by.cryptic.utils.event.order.OrderCreatedEvent;
import by.cryptic.utils.event.order.OrderSuccessEvent;
import by.cryptic.utils.event.payment.PaymentCreatedEvent;
import by.cryptic.utils.event.payment.PaymentFailedEvent;
import by.cryptic.utils.event.payment.PaymentSuccessEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SagaService {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = {"order-topic", "payment-topic"})
    public void onOrderEvent(String rawJson) throws JsonProcessingException {
        JsonNode node = objectMapper.readTree(rawJson);
        EventType eventType = EventType.valueOf(node.get("eventType").asText());
        switch (eventType) {
            case OrderSuccessEvent -> {
                OrderSuccessEvent event = objectMapper.treeToValue(node, OrderSuccessEvent.class);
                kafkaTemplate.send("order-topic", objectMapper
                        .writeValueAsString(OrderCreatedEvent.builder()
                        .orderId(event.getOrderId())
                        .price(event.getPrice())
                        .orderStatus(event.getOrderStatus())
                        .createdBy(event.getCreatedBy())
                        .createdTimestamp(event.getCreatedTimestamp())
                        .listOfProducts(event.getListOfProducts())
                        .build()));
            }
            case PaymentSuccessEvent -> {
                PaymentSuccessEvent event = objectMapper.treeToValue(node, PaymentSuccessEvent.class);
                kafkaTemplate.send("payment-topic", objectMapper
                        .writeValueAsString(PaymentCreatedEvent.builder()
                                .paymentId(event.getPaymentId())
                                .userId(event.getUserId())
                                .timestamp(event.getTimestamp())
                                .paymentMethod(event.getPaymentMethod())
                                .price(event.getPrice())
                                .orderId(event.getOrderId())
                                .paymentStatus(event.getPaymentStatus())
                                .build()));
            }
            case PaymentFailedEvent -> {
                PaymentFailedEvent event = objectMapper.treeToValue(node, PaymentFailedEvent.class);
                kafkaTemplate.send("order-topic", objectMapper
                        .writeValueAsString(OrderCanceledEvent.builder()
                                .orderId(event.getOrderId())
                                .canceledTimestamp(LocalDateTime.now())
                                .orderStatus(OrderStatus.CANCELLED)
                                .userEmail(event.getEmail())
                                .build()));
            }
        }
    }
}
