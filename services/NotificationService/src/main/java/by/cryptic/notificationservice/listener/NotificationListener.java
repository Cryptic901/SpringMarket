package by.cryptic.notificationservice.listener;

import by.cryptic.notificationservice.service.EmailContentBuilder;
import by.cryptic.notificationservice.service.EmailService;
import by.cryptic.utils.event.EventType;
import by.cryptic.utils.event.order.OrderCanceledEvent;
import by.cryptic.utils.event.order.OrderSuccessEvent;
import by.cryptic.utils.event.user.UserCreatedEvent;
import by.cryptic.utils.event.user.UserResendVerifyMessageEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationListener {

    private final EmailService emailService;
    private final EmailContentBuilder emailContentBuilder;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order-topic", groupId = "notification-group")
    public void sendOrderStatus(String orderEvent) throws JsonProcessingException, MessagingException {
        JsonNode node = objectMapper.readTree(orderEvent);
        EventType eventType = EventType.valueOf(node.get("eventType").asText());
        switch (eventType) {
            case OrderCreatedEvent -> {
                OrderSuccessEvent orderCreatedEvent = objectMapper.treeToValue(node,
                        OrderSuccessEvent.class);
                emailService.sendEmail(orderCreatedEvent.getUserEmail(), "SpringMarket Order",
                        emailContentBuilder.buildOrderEmailContent(orderCreatedEvent.getOrderId(),
                                orderCreatedEvent.getOrderStatus()));
            }
            case OrderCanceledEvent -> {
                OrderCanceledEvent orderCanceledEvent = objectMapper.treeToValue(node,
                        OrderCanceledEvent.class);
                emailService.sendEmail(orderCanceledEvent.getUserEmail(), "SpringMarket Order",
                        emailContentBuilder.buildOrderEmailContent(orderCanceledEvent.getOrderId(),
                                orderCanceledEvent.getOrderStatus()));
            }
        }
    }

    @KafkaListener(topics = "user-topic", groupId = "notification-group")
    public void sendVerificationEmail(String userEvent) throws MessagingException, JsonProcessingException {
        JsonNode node = objectMapper.readTree(userEvent);
        EventType eventType = EventType.valueOf(node.get("eventType").asText());
        switch (eventType) {
            case UserCreatedEvent -> {
                UserCreatedEvent event = objectMapper.treeToValue(node, UserCreatedEvent.class);
                emailService.sendEmail(event.getEmail(), "SpringMarket verification",
                        emailContentBuilder.buildVerificationEmailContent(event.getVerificationCode()));
            }
            case UserResendVerifyMessageEvent -> {
                UserResendVerifyMessageEvent event = objectMapper.treeToValue(node, UserResendVerifyMessageEvent.class);
                emailService.sendEmail(event.getEmail(), "SpringMarket verification",
                        emailContentBuilder.buildVerificationEmailContent(event.getVerificationCode()));
            }
        }
    }
}
