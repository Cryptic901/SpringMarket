package by.cryptic.authenticationservice.service.command.handler;

import by.cryptic.authenticationservice.model.AuthUser;
import by.cryptic.authenticationservice.repository.AuthUserRepository;
import by.cryptic.utils.CommandHandler;
import by.cryptic.utils.event.user.UserResendVerifyMessageEvent;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserResendVerifyCommandHandler implements CommandHandler<String> {

    private final AuthUserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void handle(String email) throws AuthenticationException {
        AuthUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("User not found"));
        if (user.isEnabled()) {
            throw new AuthenticationException("Account already verified");
        }
        Random random = new Random();
        Integer sixNumber = 100000 + random.nextInt(900000);
        user.setVerifyCode(sixNumber);
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);
        eventPublisher.publishEvent(UserResendVerifyMessageEvent.builder()
                .verificationCode(sixNumber)
                .userId(user.getId())
                .email(email)
                .build());
    }
}
