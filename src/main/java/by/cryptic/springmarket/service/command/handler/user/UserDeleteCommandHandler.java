package by.cryptic.springmarket.service.command.handler.user;

import by.cryptic.springmarket.event.user.UserDeletedEvent;
import by.cryptic.springmarket.model.write.AppUser;
import by.cryptic.springmarket.repository.write.AppUserRepository;
import by.cryptic.springmarket.service.command.UserDeleteCommand;
import by.cryptic.springmarket.service.command.handler.CommandHandler;
import by.cryptic.springmarket.util.AuthUtil;
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
    private final AuthUtil authUtil;
    private final CacheManager cacheManager;

    @Transactional
    public void handle(UserDeleteCommand userDeleteCommand) {
        AppUser user = authUtil.getUserFromContext();
        userRepository.deleteById(user.getId());
        eventPublisher.publishEvent(new UserDeletedEvent(user.getId()));
        Objects.requireNonNull(cacheManager.getCache("users")).evict(user.getId());
    }
}
