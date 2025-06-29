package by.cryptic.userservice.service.command.handler;

import by.cryptic.userservice.mapper.UserMapper;
import by.cryptic.userservice.model.write.AppUser;
import by.cryptic.userservice.repository.write.AppUserRepository;
import by.cryptic.userservice.service.command.UserUpdateCommand;
import by.cryptic.utils.CommandHandler;
import by.cryptic.utils.event.user.UserUpdatedEvent;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"users"})
public class UserUpdateCommandHandler implements CommandHandler<UserUpdateCommand> {
    private final UserMapper userMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final CacheManager cacheManager;
    private final AppUserRepository userRepository;

    @Transactional
    public void handle(UserUpdateCommand userUpdateCommand) {
        AppUser user = userRepository.findById(userUpdateCommand.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        userMapper.updateEntity(user, userUpdateCommand);
        eventPublisher.publishEvent(UserUpdatedEvent.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .phoneNumber(user.getPhoneNumber())
                .build());
        Objects.requireNonNull(cacheManager.getCache("users"))
                .put("user:" + userUpdateCommand.username(), user);
    }
}
