package by.cryptic.springmarket.event.handler;

import by.cryptic.springmarket.event.user.UserEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class UserEventHandler {

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    @Async("userExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserEvent(UserEvent event) {
        kafkaTemplate.send("order-topic", event);
    }
}
