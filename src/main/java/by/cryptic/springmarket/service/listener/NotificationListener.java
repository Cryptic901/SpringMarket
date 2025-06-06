package by.cryptic.springmarket.service.listener;

import by.cryptic.springmarket.event.order.OrderCreatedEvent;
import by.cryptic.springmarket.event.user.UserCreatedEvent;
import by.cryptic.springmarket.event.user.UserVerifyEvent;
import by.cryptic.springmarket.service.EmailContentBuilder;
import by.cryptic.springmarket.service.EmailService;
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

    @KafkaListener(topics = "order-topic", groupId = "notification-group")
    public void sendOrderStatus(OrderCreatedEvent orderEvent) throws MessagingException {
        log.info("Received notification: {}", orderEvent);
        emailService.sendEmail(orderEvent.getUserEmail(), "SpringMarket Order",
                emailContentBuilder.buildOrderEmailContent(orderEvent.getOrderId(), orderEvent.getStatus()));
    }

    @KafkaListener(topics = "user-topic", groupId = "notification-group")
    public void sendVerificationEmail(UserCreatedEvent event) throws MessagingException {
        emailService.sendEmail(event.getEmail(), "SpringMarket verification",
                emailContentBuilder.buildVerificationEmailContent(event.getVerificationCode()));
    }

    @KafkaListener(topics = "user-topic", groupId = "notification-group")
    public void resendVerificationEmail(UserVerifyEvent event) throws MessagingException {
        emailService.sendEmail(event.getEmail(), "SpringMarket verification",
                emailContentBuilder.buildVerificationEmailContent(event.getVerificationCode()));
    }
}
