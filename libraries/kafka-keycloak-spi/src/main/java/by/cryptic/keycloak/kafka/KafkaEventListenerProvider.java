package by.cryptic.keycloak.kafka;

import by.cryptic.utils.event.DomainEvent;
import by.cryptic.utils.event.user.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaEventListenerProvider implements EventListenerProvider {

    private final ObjectMapper objectMapper;
    private final KafkaProducer<String, String> kafkaProducer;

    @Override
    public void onEvent(Event event) {
        try {
            if (event.getUserId() == null) {
                log.warn("Event {} does not contain userId", event.getType());
                return;
            }
            DomainEvent userEvent = mapKeycloakEventToUserEvent(event);
            if (userEvent != null) {
                String json = objectMapper.writeValueAsString(userEvent);
                ProducerRecord<String, String> record = new ProducerRecord<>("user-topic", json);
                kafkaProducer.send(record, (metadata, exception) ->
                {
                    if (exception != null) {
                        log.error("Error sending event to Kafka", exception);
                    } else {
                        log.info("User event successfully sent topic={}, partition={}, offset={}",
                                metadata.topic(), metadata.partition(), metadata.offset());
                    }
                });
            }
        } catch (Exception e) {
            throw new RuntimeException("KafkaEventListenerProvider can't process this event", e);
        }
    }

    private DomainEvent mapKeycloakEventToUserEvent(Event event) throws JsonProcessingException {
        Map<String, String> details = event.getDetails();
        UUID userId = UUID.fromString(event.getUserId());
        return switch (event.getType()) {
            case REGISTER -> {
                UserCreatedEvent userCreatedEvent = UserCreatedEvent.builder()
                        .userId(userId)
                        .email(details.get("email"))
                        .username(details.get("username"))
                        .firstName(details.get("first_name"))
                        .lastName(details.get("last_name"))
                        .build();
                log.info("UserCreatedEvent from keycloak event {}", objectMapper.writeValueAsString(userCreatedEvent));
                yield userCreatedEvent;
            }
            case UPDATE_PROFILE -> {
                UserUpdatedEvent userUpdatedEvent = UserUpdatedEvent.builder()
                        .userId(userId)
                        .username(details.get("username"))
                        .phoneNumber(details.get("phoneNumber"))
                        .firstName(details.get("updated_first_name"))
                        .lastName(details.get("updated_last_name"))
                        .build();
                log.info("UserUpdatedEvent from keycloak event {}", objectMapper.writeValueAsString(userUpdatedEvent));
                yield userUpdatedEvent;
            }
            case DELETE_ACCOUNT -> {
                UserDeletedEvent userDeletedEvent = UserDeletedEvent.builder()
                        .userId(userId)
                        .build();
                log.info("UserDeletedEvent from keycloak event {}", objectMapper.writeValueAsString(userDeletedEvent));
                yield userDeletedEvent;
            }
            case LOGIN -> {
                UserLoginedEvent userLoginedEvent = UserLoginedEvent.builder()
                        .userId(userId)
                        .build();
                log.info("UserLoginedEvent from keycloak event {}", objectMapper.writeValueAsString(userLoginedEvent));
                yield userLoginedEvent;
            }
            case LOGOUT -> {
                UserLogoutEvent userLogoutEvent = UserLogoutEvent.builder()
                        .userId(userId)
                        .build();
                log.info("UserLogoutEvent from keycloak event {}", objectMapper.writeValueAsString(userLogoutEvent));
                yield userLogoutEvent;
            }
            case LOGIN_ERROR -> {
                log.error("Login unsuccessful");
                yield null;
            }
            case SEND_VERIFY_EMAIL -> {
                log.info("Verify Email sended {}", event.getType());
                yield null;
            }
            case VERIFY_EMAIL -> {
                log.info("Email Verify {}", event.getType());
                yield null;
            }
            case CODE_TO_TOKEN -> {
                log.info("Code to token {}", event.getType());
                yield null;
            }
            default -> {
                log.error("Unhandled event {}", event.getType());
                yield null;
            }
        };
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {

    }

    @Override
    public void close() {
    }
}
