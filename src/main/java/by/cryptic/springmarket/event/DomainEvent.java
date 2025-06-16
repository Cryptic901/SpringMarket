package by.cryptic.springmarket.event;

import by.cryptic.springmarket.enums.EventType;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public abstract class DomainEvent {
    private final UUID eventId;
    private final LocalDateTime timestamp;
    private final EventType eventType;

    protected DomainEvent() {
        this.eventId = UUID.randomUUID();
        this.timestamp = LocalDateTime.now();
        this.eventType = EventType.valueOf(this.getClass().getSimpleName());
    }
}
