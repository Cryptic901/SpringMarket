package by.cryptic.springmarket.event.user;

import by.cryptic.springmarket.event.DomainEvent;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDeletedEvent extends DomainEvent implements UserEvent {

    private UUID userId;
    private String version = "1.0";
    private String source = this.getClass().getName();

    public UserDeletedEvent(UUID id) {
        this.userId = id;
    }
}
