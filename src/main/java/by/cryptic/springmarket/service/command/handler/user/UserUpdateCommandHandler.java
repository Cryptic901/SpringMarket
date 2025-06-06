package by.cryptic.springmarket.service.command.handler.user;

import by.cryptic.springmarket.event.user.UserUpdatedEvent;
import by.cryptic.springmarket.mapper.UserMapper;
import by.cryptic.springmarket.model.write.AppUser;
import by.cryptic.springmarket.service.command.UserUpdateCommand;
import by.cryptic.springmarket.service.command.handler.CommandHandler;
import by.cryptic.springmarket.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"users"})
public class UserUpdateCommandHandler implements CommandHandler<UserUpdateCommand> {
    private final UserMapper userMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final AuthUtil authUtil;

    @CachePut(key = "'user:' + #userUpdateCommand.username()")
    @Async("userExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(UserUpdateCommand userUpdateCommand) {
        AppUser user = authUtil.getUserFromContext();
        userMapper.updateEntity(user, userUpdateCommand);
        eventPublisher.publishEvent(UserUpdatedEvent.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .build());
    }
}
