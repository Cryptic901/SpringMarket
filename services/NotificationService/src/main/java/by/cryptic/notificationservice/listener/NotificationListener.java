package by.cryptic.notificationservice.listener;

import by.cryptic.notificationservice.service.EmailContentBuilder;
import by.cryptic.notificationservice.service.EmailService;
import by.cryptic.utils.event.DomainEvent;
import by.cryptic.utils.event.order.OrderCanceledEvent;
import by.cryptic.utils.event.order.OrderCreatedEvent;
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
            case OrderCreatedEvent orderCreatedEvent ->
                    emailService.sendEmail(orderCreatedEvent.getUserEmail(), "SpringMarket Order",
                            emailContentBuilder.buildOrderEmailContent(orderCreatedEvent.getOrderId(),
                                    orderCreatedEvent.getOrderStatus()));

            case OrderCanceledEvent orderCanceledEvent ->
                    emailService.sendEmail(orderCanceledEvent.getUserEmail(), "SpringMarket Order",
                            emailContentBuilder.buildOrderEmailContent(orderCanceledEvent.getOrderId(),
                                    orderCanceledEvent.getOrderStatus()));

            default -> throw new IllegalStateException("Unexpected event type: " + event);
        }
    }
}