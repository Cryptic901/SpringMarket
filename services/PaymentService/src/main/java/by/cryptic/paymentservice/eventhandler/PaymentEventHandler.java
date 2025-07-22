package by.cryptic.paymentservice.eventhandler;

import by.cryptic.utils.event.DomainEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PaymentEventHandler {

    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;

    @Async("paymentExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderEvent(DomainEvent event) {
        kafkaTemplate.send("payment-topic", event);
    }
}
