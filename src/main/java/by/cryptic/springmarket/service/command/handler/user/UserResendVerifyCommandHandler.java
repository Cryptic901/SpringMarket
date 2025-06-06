package by.cryptic.springmarket.service.command.handler.user;

import by.cryptic.springmarket.event.user.UserVerifyEvent;
import by.cryptic.springmarket.model.write.AppUser;
import by.cryptic.springmarket.repository.write.AppUserRepository;
import by.cryptic.springmarket.service.command.handler.CommandHandler;
import by.cryptic.springmarket.util.AuthUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class UserResendVerifyCommandHandler implements CommandHandler<String> {

    private final AppUserRepository userRepository;
    private final AuthUtil authUtil;
    private final ApplicationEventPublisher eventPublisher;

    public void handle(String email) throws MessagingException, AuthenticationException {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("User not found"));
        if (user.isEnabled()) {
            throw new AuthenticationException("Account already verified");
        }
        Integer sixNumber = authUtil.generateRandomSixNumber();
        user.setVerifyCode(sixNumber);
        user.setVerificationCodeExpiresAt(OffsetDateTime.now().plusMinutes(15));
        userRepository.save(user);
        eventPublisher.publishEvent(UserVerifyEvent.builder()
                .verificationCode(sixNumber)
                .userId(user.getId())
                .email(email)
                .build());
    }
}
