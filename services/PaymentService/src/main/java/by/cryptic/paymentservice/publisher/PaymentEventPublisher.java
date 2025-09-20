package by.cryptic.paymentservice.publisher;

import by.cryptic.paymentservice.model.write.OutboxEntity;
import by.cryptic.paymentservice.model.write.Payment;
import by.cryptic.paymentservice.repository.write.OutboxRepository;
import by.cryptic.utils.enums.PaymentStatus;
import by.cryptic.utils.event.DomainEvent;
import by.cryptic.utils.event.EventType;
import by.cryptic.utils.event.payment.PaymentCreatedEvent;
import by.cryptic.utils.event.payment.PaymentFailedEvent;
import by.cryptic.utils.event.payment.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentEventPublisher {

    private final OutboxRepository outboxRepository;

    private void saveToOutbox(UUID aggregateId, EventType eventType, DomainEvent event) {
        OutboxEntity outbox = OutboxEntity.builder()
                .aggregateId(aggregateId)
                .aggregateType("payment")
                .eventType(String.valueOf(eventType))
                .payload(event)
                .build();
        outboxRepository.save(outbox);
    }

    public void sentPaymentSuccessEvent(Payment payment) {
        saveToOutbox(payment.getId(),
                EventType.PaymentSuccessEvent,
                PaymentSuccessEvent.builder()
                        .paymentId(payment.getId())
                        .paymentMethod(payment.getPaymentMethod())
                        .price(payment.getPrice())
                        .orderId(payment.getOrderId())
                        .userId(payment.getUserId())
                        .paymentStatus(PaymentStatus.SUCCESS)
                        .build());
    }

    public void sentPaymentFailedEvent(PaymentCreatedEvent command) {
        saveToOutbox(command.getPaymentId(),
                EventType.PaymentFailedEvent,
                PaymentFailedEvent.builder()
                        .paymentId(null)
                        .paymentMethod(command.getPaymentMethod())
                        .price(command.getPrice())
                        .orderId(command.getOrderId())
                        .userId(command.getUserId())
                        .email(command.getUserEmail())
                        .paymentStatus(PaymentStatus.FAILED)
                        .build());
    }
}
