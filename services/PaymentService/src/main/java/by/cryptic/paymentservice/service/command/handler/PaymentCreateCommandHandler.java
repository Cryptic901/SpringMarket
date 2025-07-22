package by.cryptic.paymentservice.service.command.handler;

import by.cryptic.paymentservice.model.write.Payment;
import by.cryptic.paymentservice.repository.write.PaymentRepository;
import by.cryptic.paymentservice.service.command.PaymentCreateCommand;
import by.cryptic.utils.CommandHandler;
import by.cryptic.utils.PaymentStatus;
import by.cryptic.utils.event.payment.PaymentFailedEvent;
import by.cryptic.utils.event.payment.PaymentSuccessEvent;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"payments"})
public class PaymentCreateCommandHandler implements CommandHandler<PaymentCreateCommand> {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final PaymentRepository paymentRepository;

    @Override
    @Retry(name = "paymentRetry", fallbackMethod = "paymentFallback")
    @Bulkhead(name = "paymentBulkhead", fallbackMethod = "paymentFallback")
    public void handle(PaymentCreateCommand command) {

        Payment payment = Payment.builder()
                .paymentMethod(command.paymentMethod())
                .timestamp(LocalDateTime.now())
                .price(command.price())
                .orderId(command.orderId())
                .userId(command.userId())
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
    }

    public void paymentFallback(PaymentCreateCommand command, Throwable t) {
        log.error("Failed to create payment {}", t.getMessage());
        PaymentFailedEvent failedEvent = PaymentFailedEvent.builder()
                .paymentId(null)
                .paymentMethod(command.paymentMethod())
                .price(command.price())
                .orderId(command.orderId())
                .userId(command.userId())
                .email(command.email())
                .paymentStatus(PaymentStatus.FAILED)
                .build();
        applicationEventPublisher.publishEvent(failedEvent);
    }
}

