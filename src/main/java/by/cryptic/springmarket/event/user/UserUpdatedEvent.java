package by.cryptic.springmarket.event.user;

import by.cryptic.springmarket.event.DomainEvent;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdatedEvent extends DomainEvent implements UserEvent {

    private UUID userId;
    private String username;
    private String phoneNumber;
    private String email;
    private String version = "1.0";
    private String source = this.getClass().getName();
}
