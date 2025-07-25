package by.cryptic.utils.event.user;

import by.cryptic.utils.event.DomainEvent;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDeletedEvent extends DomainEvent implements UserEvent {

    private UUID userId;
    private static final String version = "1.0";
    @Builder.Default
    private String source = UserDeletedEvent.class.getName();

    public UserDeletedEvent(UUID id) {
        this.userId = id;
    }
}
