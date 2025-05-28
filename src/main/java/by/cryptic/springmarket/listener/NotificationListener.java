package by.cryptic.springmarket.listener;

import by.cryptic.springmarket.event.OrderEvent;
import by.cryptic.springmarket.service.EmailContentBuilder;
import by.cryptic.springmarket.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationListener {

    private final EmailService emailService;
    private final EmailContentBuilder emailContentBuilder;

    @KafkaListener(topics = "order-topic", groupId = "notification-group")
    public void listenOrders(OrderEvent orderEvent) throws MessagingException {
        log.info("Received order event: {}", orderEvent);
        emailService.sendEmail(orderEvent.getUserEmail(), "SpringMarket Order",
                emailContentBuilder.buildOrderEmailContent(orderEvent.getOrderId(), orderEvent.getStatus()));
        }
    }
