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
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserEventListener {

    private final UserViewRepository userViewRepository;
    private final AppUserRepository appUserRepository;
    private final ObjectMapper objectMapper;
    private final UserMapper userMapper;

    @KafkaListener(topics = "user-topic", groupId = "user-group")
    public void listenUserEvents(String rawEvent) {
        try {
            log.info("Received event: {}", rawEvent);
            DomainEvent event = objectMapper.readValue(rawEvent, DomainEvent.class);
            switch (event) {
                case UserCreatedEvent createdEvent -> {
                    AppUserView view = AppUserView.builder()
                            .username(createdEvent.getUsername())
                            .createdAt(createdEvent.getTimestamp())
                            .userId(createdEvent.getUserId())
                            .build();
                    userViewRepository.save(view);
                    AppUser user = AppUser.builder()
                            .username(createdEvent.getUsername())
                            .createdAt(createdEvent.getTimestamp())
                            .build();
                    appUserRepository.save(user);
                }
                case UserUpdatedEvent userUpdatedEvent ->
                        userViewRepository.findById(userUpdatedEvent.getUserId()).ifPresent(userView -> {
                            userMapper.updateView(userView, userUpdatedEvent);
                            userViewRepository.save(userView);
                        });
                case UserDeletedEvent userDeletedEvent ->
                        userViewRepository.findById(userDeletedEvent.getUserId()).ifPresent(_ -> userViewRepository.deleteById(userDeletedEvent.getUserId()));
                default -> throw new IllegalStateException("Unexpected event type: " + event.getEventType());
            }
        } catch (
                Exception e) {
            log.error("Failed to process event {}, {}", rawEvent, e);
            throw new RuntimeException("Catched exception: ", e);
        }
    }
}
