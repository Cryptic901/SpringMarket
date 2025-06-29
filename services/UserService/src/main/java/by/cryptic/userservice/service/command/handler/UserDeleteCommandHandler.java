package by.cryptic.userservice.service.command.handler;

import by.cryptic.userservice.repository.write.AppUserRepository;
import by.cryptic.userservice.service.command.UserDeleteCommand;
import by.cryptic.utils.CommandHandler;
import by.cryptic.utils.event.user.UserDeletedEvent;
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
public class UserDeleteCommandHandler implements CommandHandler<UserDeleteCommand> {

    private final AppUserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CacheManager cacheManager;

    @Transactional
    public void handle(UserDeleteCommand userDeleteCommand) {
        userRepository.deleteById(userDeleteCommand.id());
        eventPublisher.publishEvent(new UserDeletedEvent(userDeleteCommand.id()));
        Objects.requireNonNull(cacheManager.getCache("users")).evict(userDeleteCommand.id());
    }
}
