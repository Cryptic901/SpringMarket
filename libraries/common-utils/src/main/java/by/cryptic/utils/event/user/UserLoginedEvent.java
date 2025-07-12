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
    private static final String version = "1.0";
    @Builder.Default
    private String source = UserLoginedEvent.class.getName();

    public UserLoginedEvent(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
