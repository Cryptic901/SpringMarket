package by.cryptic.productservice.eventhandler;

import by.cryptic.utils.event.DomainEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ProductEventHandler {

    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;

    @Async("productExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProductEvent(DomainEvent event) {
        kafkaTemplate.send("product-topic", event);
    }
}
