package by.cryptic.authenticationservice.service.command.handler;

import by.cryptic.authenticationservice.dto.VerifyUserDTO;
import by.cryptic.authenticationservice.model.AuthUser;
import by.cryptic.authenticationservice.repository.AuthUserRepository;
import by.cryptic.utils.CommandHandler;
import by.cryptic.utils.event.user.UserResendVerifyMessageEvent;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserVerifyCommandHandler implements CommandHandler<VerifyUserDTO> {
    private final AuthUserRepository userRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void handle(VerifyUserDTO verifyUserDto) throws AuthenticationException {
        AuthUser user = userRepository.findByEmail(verifyUserDto.email())
                .orElseThrow(() -> new AuthenticationException("User with email not found %s".formatted(verifyUserDto.email())));
        if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AuthenticationException("Verification has expired. Please register again.");
        }
        if (user.getVerifyCode().equals(verifyUserDto.verificationCode())) {
            user.setEnabled(true);
            user.setVerificationCodeExpiresAt(null);
            user.setVerifyCode(null);
            userRepository.save(user);
        } else {
            throw new AuthenticationException("Invalid verification code.");
        }
        applicationEventPublisher.publishEvent(UserResendVerifyMessageEvent.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .verificationCode(verifyUserDto.verificationCode())
                .build());
    }
}
