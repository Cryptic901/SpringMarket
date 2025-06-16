package by.cryptic.springmarket.service.command.handler.user;

import by.cryptic.springmarket.dto.VerifyUserDTO;
import by.cryptic.springmarket.event.user.UserResendVerifyMessageEvent;
import by.cryptic.springmarket.model.write.AppUser;
import by.cryptic.springmarket.repository.write.AppUserRepository;
import by.cryptic.springmarket.service.command.handler.CommandHandler;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserVerifyCommandHandler implements CommandHandler<VerifyUserDTO> {
    private final AppUserRepository userRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void handle(VerifyUserDTO verifyUserDto) throws AuthenticationException {
        AppUser user = userRepository.findByEmail(verifyUserDto.email())
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
