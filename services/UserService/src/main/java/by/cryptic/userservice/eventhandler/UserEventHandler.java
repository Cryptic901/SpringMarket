package by.cryptic.userservice.eventhandler;

import by.cryptic.utils.event.DomainEvent;
import by.cryptic.utils.event.user.UserEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class UserEventHandler {

    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;

    @Async("userExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserEvent(DomainEvent event) {
        kafkaTemplate.send("user-topic", event);
    }
}
