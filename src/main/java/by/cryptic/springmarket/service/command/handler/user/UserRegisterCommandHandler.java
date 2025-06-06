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
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"users"})
public class UserRegisterCommandHandler implements CommandHandler<UserRegisterCommand> {

    private final PasswordEncoder passwordEncoder;
    private final AppUserRepository appUserRepository;
    private final AuthUtil authUtil;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @CachePut(key = "'user:' + #registerUserDto.username()")
    public void handle(UserRegisterCommand registerUserDto) {
        AppUser user = new AppUser(registerUserDto.username(), registerUserDto.email(),
                passwordEncoder.encode(registerUserDto.password()),
                Role.ROLE_USER, registerUserDto.phoneNumber(), registerUserDto.gender());
        createDefaultUser(user);

        eventPublisher.publishEvent(UserCreatedEvent.builder()
                .phoneNumber(user.getPhoneNumber())
                .userId(user.getId())
                .createdAt(user.getCreatedAt())
                .gender(user.getGender())
                .username(user.getUsername())
                .email(user.getEmail())
                .verificationCode(user.getVerifyCode())
                .build());
    }

    private void createDefaultUser(AppUser user) {
        Cart cart = Cart.builder()
                .user(user)
                .build();
        user.setCart(cart);
        user.setVerifyCode(authUtil.generateRandomSixNumber());
        user.setVerificationCodeExpiresAt(OffsetDateTime.now().plusMinutes(15));
        user.setEnabled(false);
        appUserRepository.save(user);
    }
}
