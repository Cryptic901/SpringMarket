package by.cryptic.utils.event.user;

import by.cryptic.utils.event.DomainEvent;
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
    private static final String version = "1.0";
    @Builder.Default
    private String source = UserResendVerifyMessageEvent.class.getName();
}
