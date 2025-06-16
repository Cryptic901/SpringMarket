package by.cryptic.springmarket.service.command.handler.user;

import by.cryptic.springmarket.enums.Role;
import by.cryptic.springmarket.event.user.UserCreatedEvent;
import by.cryptic.springmarket.model.write.AppUser;
import by.cryptic.springmarket.model.write.Cart;
import by.cryptic.springmarket.repository.write.AppUserRepository;
import by.cryptic.springmarket.service.command.UserRegisterCommand;
import by.cryptic.springmarket.service.command.handler.CommandHandler;
import by.cryptic.springmarket.util.AuthUtil;
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

@Service
@RequiredArgsConstructor
@Slf4j
@CacheConfig(cacheNames = {"users"})
public class UserRegisterCommandHandler implements CommandHandler<UserRegisterCommand> {

    private final PasswordEncoder passwordEncoder;
    private final AppUserRepository appUserRepository;
    private final AuthUtil authUtil;
    private final ApplicationEventPublisher eventPublisher;
    private final CacheManager cacheManager;

    @Transactional(rollbackFor = Exception.class)
    public void handle(UserRegisterCommand registerUserDto) {
        AppUser user = new AppUser(registerUserDto.username(), registerUserDto.email(),
                passwordEncoder.encode(registerUserDto.password()),
                Role.ROLE_USER, registerUserDto.phoneNumber(), registerUserDto.gender());
        createDefaultUser(user);
        UserCreatedEvent event = UserCreatedEvent.builder()
                .phoneNumber(user.getPhoneNumber())
                .userId(user.getId())
                .createdAt(user.getCreatedAt())
                .gender(user.getGender())
                .username(user.getUsername())
                .email(user.getEmail())
                .verificationCode(user.getVerifyCode())
                .build();
        eventPublisher.publishEvent(event);
        Objects.requireNonNull(cacheManager.getCache("users"))
                .put("user:" + registerUserDto.username(), user);
    }

    private void createDefaultUser(AppUser user) {
        Cart cart = Cart.builder()
                .user(user)
                .build();
        user.setCart(cart);
        user.setVerifyCode(authUtil.generateRandomSixNumber());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        user.setEnabled(false);
        appUserRepository.save(user);
    }
}
