package by.cryptic.springmarket.service.listener;

import by.cryptic.springmarket.enums.EventType;
import by.cryptic.springmarket.event.user.UserCreatedEvent;
import by.cryptic.springmarket.event.user.UserDeletedEvent;
import by.cryptic.springmarket.event.user.UserUpdatedEvent;
import by.cryptic.springmarket.mapper.UserMapper;
import by.cryptic.springmarket.model.read.AppUserView;
import by.cryptic.springmarket.repository.read.UserViewRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
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
    private final ObjectMapper objectMapper;
    private final UserMapper userMapper;

    @KafkaListener(topics = "user-topic", groupId = "user-group")
    public void listenUsers(String rawEvent) throws JsonProcessingException {
        log.info("Received event: {}", rawEvent);
        JsonNode node = objectMapper.readTree(rawEvent);
        String type = node.get("eventType").asText();
        log.info("Received event type {}", type);
        switch (EventType.valueOf(type)) {
            case UserCreatedEvent -> {
                UserCreatedEvent event = objectMapper.treeToValue(node, UserCreatedEvent.class);
                AppUserView view = AppUserView.builder()
                        .email(event.getEmail())
                        .username(event.getUsername())
                        .createdAt(event.getTimestamp())
                        .userId(event.getUserId())
                        .gender(event.getGender())
                        .phoneNumber(event.getPhoneNumber())
                        .build();
                userViewRepository.save(view);
            }
            case UserUpdatedEvent -> {
                UserUpdatedEvent event = objectMapper.treeToValue(node, UserUpdatedEvent.class);
                userViewRepository.findById(event.getUserId()).ifPresent(userView -> {
                    userMapper.updateView(userView, event);
                    userViewRepository.save(userView);
                });
            }
            case UserDeletedEvent -> {
                UserDeletedEvent event = objectMapper.treeToValue(node, UserDeletedEvent.class);
                userViewRepository.findById(event.getUserId()).ifPresent(userView -> userViewRepository.deleteById(event.getUserId()));
            }
            default -> throw new IllegalStateException("Unexpected event type: " + type);
        }
    }
}
