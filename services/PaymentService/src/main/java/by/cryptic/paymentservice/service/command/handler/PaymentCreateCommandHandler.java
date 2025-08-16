package by.cryptic.paymentservice.service.command.handler;

import by.cryptic.paymentservice.model.write.Payment;
import by.cryptic.paymentservice.repository.write.PaymentRepository;
import by.cryptic.utils.CommandHandler;
import by.cryptic.utils.PaymentStatus;
import by.cryptic.utils.event.payment.PaymentCreatedEvent;
import by.cryptic.utils.event.payment.PaymentFailedEvent;
import by.cryptic.utils.event.payment.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentCreateCommandHandler implements CommandHandler<PaymentCreatedEvent> {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final PaymentRepository paymentRepository;

    @Override
    @KafkaListener(topics = "saga-topic")
    @CachePut(cacheNames = "payments", key = "'payment:' + #command.paymentId()")
    public void handle(PaymentCreatedEvent command) {
        try {
            //Change to real payment processing
            Payment payment = Payment.builder()
                    .paymentMethod(command.getPaymentMethod())
                    .timestamp(LocalDateTime.now())
                    .price(command.getPrice())
                    .orderId(command.getOrderId())
                    .userId(command.getUserId())
                    .build();
            Payment savedPayment = paymentRepository.save(payment);

            PaymentSuccessEvent successEvent = PaymentSuccessEvent.builder()
                    .paymentId(savedPayment.getId())
                    .paymentMethod(savedPayment.getPaymentMethod())
                    .price(savedPayment.getPrice())
                    .orderId(savedPayment.getOrderId())
                    .userId(savedPayment.getUserId())
                    .paymentStatus(PaymentStatus.SUCCESS)
                    .build();
            applicationEventPublisher.publishEvent(successEvent);
        } catch (Exception e) {
            log.error("Failed to create payment {}", e.getMessage());
            PaymentFailedEvent failedEvent = PaymentFailedEvent.builder()
                    .paymentId(null)
                    .paymentMethod(command.getPaymentMethod())
                    .price(command.getPrice())
                    .orderId(command.getOrderId())
                    .userId(command.getUserId())
                    .email(command.getUserEmail())
                    .paymentStatus(PaymentStatus.FAILED)
                    .build();
            applicationEventPublisher.publishEvent(failedEvent);
        }
    }
}

