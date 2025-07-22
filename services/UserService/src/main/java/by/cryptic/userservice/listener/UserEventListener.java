package by.cryptic.userservice.listener;

import by.cryptic.userservice.mapper.UserMapper;
import by.cryptic.userservice.model.read.AppUserView;
import by.cryptic.userservice.model.write.AppUser;
import by.cryptic.userservice.repository.read.UserViewRepository;
import by.cryptic.userservice.repository.write.AppUserRepository;
import by.cryptic.utils.event.DomainEvent;
import by.cryptic.utils.event.user.UserCreatedEvent;
import by.cryptic.utils.event.user.UserDeletedEvent;
import by.cryptic.utils.event.user.UserUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserEventListener {

    private final AppUserRepository appUserRepository;
    private final UserViewRepository viewRepository;
    private final UserMapper userMapper;

    @KafkaListener(topics = "user-topic", groupId = "user-group")
    public void listenUserEvents(DomainEvent event) {
        log.debug("Received event: {}", event);
        switch (event) {
            case UserCreatedEvent createdEvent -> {
                appUserRepository.save(AppUser.builder()
                        .id(createdEvent.getUserId())
                        .username(createdEvent.getUsername())
                        .createdAt(createdEvent.getTimestamp())
                        .firstName(createdEvent.getFirstName())
                        .lastName(createdEvent.getLastName())
                        .build());
                viewRepository.save(AppUserView.builder()
                        .userId(createdEvent.getUserId())
                        .username(createdEvent.getUsername())
                        .createdAt(createdEvent.getTimestamp())
                        .firstName(createdEvent.getFirstName())
                        .lastName(createdEvent.getLastName())
                        .build());
            }

            case UserUpdatedEvent userUpdatedEvent -> {
                appUserRepository.findById(userUpdatedEvent.getUserId()).ifPresent(appUser -> {
                    userMapper.updateEntity(appUser, userUpdatedEvent);
                    appUserRepository.save(appUser);
                });
                viewRepository.findById(userUpdatedEvent.getUserId()).ifPresent(view -> {
                    userMapper.updateView(view, userUpdatedEvent);
                    viewRepository.save(view);
                });
            }

            case UserDeletedEvent userDeletedEvent -> {
                appUserRepository.findById(userDeletedEvent.getUserId())
                        .ifPresent(_ -> appUserRepository.deleteById(
                                userDeletedEvent.getUserId()));

                viewRepository.findById(userDeletedEvent.getUserId())
                        .ifPresent(_ -> viewRepository.deleteById(
                                userDeletedEvent.getUserId()));
            }

            default -> log.warn("Unexpected event type {} ", event);
        }
    }
}
