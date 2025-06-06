package by.cryptic.springmarket.event.handler;

import by.cryptic.springmarket.event.product.ProductEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ProductEventHandler {

    private final KafkaTemplate<String, ProductEvent> kafkaTemplate;

    @Async("productExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderEvent(ProductEvent event) {
        kafkaTemplate.send("product-topic", event);
    }
}
