package by.cryptic.utils.event.user;

import by.cryptic.utils.event.DomainEvent;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginedEvent extends DomainEvent {
    private String username;
    private String password;
}
