package by.cryptic.utils.event.user;

import by.cryptic.utils.event.DomainEvent;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreatedEvent extends DomainEvent implements UserEvent {

    private UUID userId;
    private String email;
    private String username;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private Character gender;
    private Integer verificationCode;
    private static final String version = "1.0";
    @Builder.Default
    private String source = UserCreatedEvent.class.getName();
}
