package by.cryptic.springmarket.event.user;

import by.cryptic.springmarket.event.DomainEvent;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResendVerifyMessageEvent extends DomainEvent implements UserEvent {
    private UUID userId;
    private String email;
    private Integer verificationCode;
}
