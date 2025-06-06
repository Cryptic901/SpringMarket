package by.cryptic.springmarket.event.user;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVerifyEvent {
    private UUID userId;
    private String email;
    private Integer verificationCode;
}
