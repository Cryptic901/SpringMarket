package by.cryptic.orderservice.listener;

import by.cryptic.orderservice.mapper.UserUpdateMapper;
import by.cryptic.orderservice.model.write.UserReplica;
import by.cryptic.orderservice.repository.write.UserReplicaRepository;
import by.cryptic.utils.event.EventType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCreatedEventListener {

    private final ObjectMapper objectMapper;
    private final UserReplicaRepository userReplicaRepository;
    private final UserUpdateMapper userUpdateMapper;

    @KafkaListener(topics = "user-topic", groupId = "order-group")
    public void listenOrders(String rawEvent) throws JsonProcessingException {
        JsonNode node = objectMapper.readTree(rawEvent);
        String type = node.get("eventType").asText();
        if (type.equals(EventType.UserCreatedEvent.name())) {
            UserCreatedEvent event = objectMapper.treeToValue(node, UserCreatedEvent.class);
            userReplicaRepository.save(UserReplica.builder()
                    .id(event.getUserId())
                    .email(event.getEmail())
                    .build());
        }
        if (type.equals(EventType.UserUpdatedEvent.name())) {
            UserUpdatedEvent event = objectMapper.treeToValue(node, UserUpdatedEvent.class);
            UserReplica userReplica = userReplicaRepository.findById(event.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User replica not found with id: " + event.getUserId()));
            userUpdateMapper.updateReplica(userReplica, event);
        }
    }
}

