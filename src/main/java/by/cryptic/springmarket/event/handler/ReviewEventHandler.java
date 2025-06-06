package by.cryptic.springmarket.event.handler;

import by.cryptic.springmarket.event.review.ReviewEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ReviewEventHandler {

    private final KafkaTemplate<String, ReviewEvent> kafkaTemplate;

    @Async("orderExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderEvent(ReviewEvent event) {
        kafkaTemplate.send("review-topic", event);
    }
}
