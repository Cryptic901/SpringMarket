package by.cryptic.paymentservice.listener;

import by.cryptic.paymentservice.model.read.PaymentView;
import by.cryptic.paymentservice.repository.read.PaymentViewRepository;
import by.cryptic.utils.event.DomainEvent;
import by.cryptic.utils.event.payment.PaymentFailedEvent;
import by.cryptic.utils.event.payment.PaymentSuccessEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
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
        DomainEvent event = objectMapper.readValue(rawEvent, DomainEvent.class);
        switch (event) {
            case PaymentSuccessEvent paymentSuccessEvent -> paymentViewRepository.save(PaymentView.builder()
                    .id(paymentSuccessEvent.getPaymentId())
                    .paymentMethod(paymentSuccessEvent.getPaymentMethod())
                    .price(paymentSuccessEvent.getPrice())
                    .orderId(paymentSuccessEvent.getOrderId())
                    .timestamp(paymentSuccessEvent.getTimestamp())
                    .userId(paymentSuccessEvent.getUserId())
                    .paymentStatus(paymentSuccessEvent.getPaymentStatus())
                    .build());

            case PaymentFailedEvent paymentFailedEvent -> paymentViewRepository.save(PaymentView.builder()
                    .id(paymentFailedEvent.getPaymentId())
                    .paymentMethod(paymentFailedEvent.getPaymentMethod())
                    .price(paymentFailedEvent.getPrice())
                    .orderId(paymentFailedEvent.getOrderId())
                    .timestamp(paymentFailedEvent.getTimestamp())
                    .userId(paymentFailedEvent.getUserId())
                    .paymentStatus(paymentFailedEvent.getPaymentStatus())
                    .build());

            default -> throw new IllegalStateException("Unexpected event type: " + event);
        }
    }
}
