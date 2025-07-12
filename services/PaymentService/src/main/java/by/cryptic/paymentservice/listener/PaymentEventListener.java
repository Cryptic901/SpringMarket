package by.cryptic.paymentservice.listener;

import by.cryptic.paymentservice.model.read.PaymentView;
import by.cryptic.paymentservice.repository.read.PaymentViewRepository;
import by.cryptic.utils.event.EventType;
import by.cryptic.utils.event.payment.PaymentFailedEvent;
import by.cryptic.utils.event.payment.PaymentSuccessEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentEventListener {


    private final ObjectMapper objectMapper;
    private final PaymentViewRepository paymentViewRepository;

    @KafkaListener(topics = "payment-topic", groupId = "payment-group")
    public void listenPayments(String rawEvent) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(rawEvent);
        EventType eventType = EventType.valueOf(jsonNode.get("eventType").asText());

        switch (eventType) {
            case PaymentSuccessEvent -> {
                PaymentSuccessEvent successEvent = objectMapper.treeToValue(jsonNode, PaymentSuccessEvent.class);
                paymentViewRepository.save(PaymentView.builder()
                        .id(successEvent.getPaymentId())
                        .paymentMethod(successEvent.getPaymentMethod())
                        .price(successEvent.getPrice())
                        .orderId(successEvent.getOrderId())
                        .timestamp(successEvent.getTimestamp())
                        .userId(successEvent.getUserId())
                        .paymentStatus(successEvent.getPaymentStatus())
                        .build());
            }
            case PaymentFailedEvent -> {
                PaymentFailedEvent failedEvent = objectMapper.treeToValue(jsonNode, PaymentFailedEvent.class);
                paymentViewRepository.save(PaymentView.builder()
                        .id(failedEvent.getPaymentId())
                        .paymentMethod(failedEvent.getPaymentMethod())
                        .price(failedEvent.getPrice())
                        .orderId(failedEvent.getOrderId())
                        .timestamp(failedEvent.getTimestamp())
                        .userId(failedEvent.getUserId())
                        .paymentStatus(failedEvent.getPaymentStatus())
                        .build());
            }
            default -> throw new IllegalStateException("Unexpected event type: " + eventType.name());
        }
    }
}
