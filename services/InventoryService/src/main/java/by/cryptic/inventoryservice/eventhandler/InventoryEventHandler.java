package by.cryptic.inventoryservice.eventhandler;

import by.cryptic.utils.event.DomainEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class InventoryEventHandler {

    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;

    @Async("paymentExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderEvent(DomainEvent event) {
        kafkaTemplate.send("inventory-topic", event);
    }
}

