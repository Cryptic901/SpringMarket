package by.cryptic.reviewservice.eventhandler;

import by.cryptic.utils.event.DomainEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ReviewEventHandler {

    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;

    @Async("reviewExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleReviewEvent(DomainEvent event) {
        kafkaTemplate.send("review-topic", event);
    }
}
