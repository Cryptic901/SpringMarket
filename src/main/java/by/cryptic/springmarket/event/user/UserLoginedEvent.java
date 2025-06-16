package by.cryptic.springmarket.event.user;

import by.cryptic.springmarket.event.DomainEvent;
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
