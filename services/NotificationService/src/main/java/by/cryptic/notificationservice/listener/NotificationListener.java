package by.cryptic.notificationservice.listener;

import by.cryptic.notificationservice.service.EmailContentBuilder;
import by.cryptic.notificationservice.service.EmailService;
import by.cryptic.utils.event.DomainEvent;
import by.cryptic.utils.event.order.OrderSuccessEvent;
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
    public void sendOrderStatus(DomainEvent event) throws MessagingException {
        switch (event) {
            case OrderSuccessEvent orderSuccessEvent ->
                    emailService.sendEmail(orderSuccessEvent.getUserEmail(), "SpringMarket Order",
                            emailContentBuilder.buildOrderEmailContent(orderSuccessEvent.getOrderId(),
                                    orderSuccessEvent.getOrderStatus()));

            default -> throw new IllegalStateException("Unexpected event type: " + event);
        }
    }
}