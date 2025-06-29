package by.cryptic.authenticationservice.service.command.handler;

import by.cryptic.authenticationservice.model.AuthUser;
import by.cryptic.authenticationservice.repository.AuthUserRepository;
import by.cryptic.authenticationservice.service.command.UserRegisterCommand;
import by.cryptic.utils.CommandHandler;
import by.cryptic.utils.Role;
import by.cryptic.utils.event.user.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
@CacheConfig(cacheNames = {"users"})
public class UserRegisterCommandHandler implements CommandHandler<UserRegisterCommand> {

    private final PasswordEncoder passwordEncoder;
    private final AuthUserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CacheManager cacheManager;

    @Transactional(rollbackFor = Exception.class)
    public void handle(UserRegisterCommand registerUserDto) {
        AuthUser user = new AuthUser(registerUserDto.username(), registerUserDto.email(),
                passwordEncoder.encode(registerUserDto.password()),
                Role.ROLE_USER);
        createDefaultUser(user);
        UserCreatedEvent event = UserCreatedEvent.builder()
                .userId(user.getId())
                .createdAt(LocalDateTime.now())
                .username(user.getUsername())
                .email(user.getEmail())
                .verificationCode(user.getVerifyCode())
                .build();
        eventPublisher.publishEvent(event);
        Objects.requireNonNull(cacheManager.getCache("users"))
                .put("user:" + registerUserDto.username(), user);
    }

    private void createDefaultUser(AuthUser user) {
        Random random = new Random();
//        Cart cart = Cart.builder()
//                .user(user)
//                .build();
//        user.setCart(cart);
        user.setVerifyCode(100000 + random.nextInt(900000));
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        user.setEnabled(false);
        userRepository.save(user);
    }
}
