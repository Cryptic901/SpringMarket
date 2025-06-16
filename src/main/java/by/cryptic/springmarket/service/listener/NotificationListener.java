package by.cryptic.springmarket.service.listener;

import by.cryptic.springmarket.enums.EventType;
import by.cryptic.springmarket.event.order.OrderCanceledEvent;
import by.cryptic.springmarket.event.order.OrderCreatedEvent;
import by.cryptic.springmarket.event.user.UserCreatedEvent;
import by.cryptic.springmarket.event.user.UserResendVerifyMessageEvent;
import by.cryptic.springmarket.service.EmailContentBuilder;
import by.cryptic.springmarket.service.EmailService;
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
                OrderCreatedEvent orderCreatedEvent = objectMapper.treeToValue(node,
                        OrderCreatedEvent.class);
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
