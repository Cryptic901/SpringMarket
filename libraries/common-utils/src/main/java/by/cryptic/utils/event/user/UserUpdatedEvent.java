package by.cryptic.utils.event.user;

import by.cryptic.utils.event.DomainEvent;
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
    private String firstName;
    private String lastName;
    private static final String version = "1.0";
    @Builder.Default
    private String source = UserUpdatedEvent.class.getName();
}
