package by.cryptic.paymentservice.listener;

import by.cryptic.exceptions.CreatingException;
import by.cryptic.paymentservice.model.read.PaymentView;
import by.cryptic.paymentservice.model.write.Payment;
import by.cryptic.paymentservice.publisher.PaymentEventPublisher;
import by.cryptic.paymentservice.repository.read.PaymentViewRepository;
import by.cryptic.paymentservice.repository.write.PaymentRepository;
import by.cryptic.utils.enums.PaymentStatus;
import by.cryptic.utils.event.DomainEvent;
import by.cryptic.utils.event.order.FinalizeOrderEvent;
import by.cryptic.utils.event.payment.PaymentCreatedEvent;
import by.cryptic.utils.event.payment.PaymentFailedEvent;
import by.cryptic.utils.event.payment.PaymentSuccessEvent;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentEventListener {

    private final PaymentViewRepository paymentViewRepository;
    private final PaymentRepository paymentRepository;
    private final CacheManager cacheManager;
    private final PaymentEventPublisher paymentEventPublisher;

    @KafkaListener(topics = "payment-topic")
    public void listenPayments(DomainEvent event) {
        switch (event) {
            case PaymentSuccessEvent paymentSuccessEvent -> {
                paymentViewRepository.save(PaymentView.builder()
                        .paymentId(paymentSuccessEvent.getPaymentId())
                        .paymentMethod(paymentSuccessEvent.getPaymentMethod())
                        .price(paymentSuccessEvent.getPrice())
                        .orderId(paymentSuccessEvent.getOrderId())
                        .timestamp(paymentSuccessEvent.getTimestamp())
                        .userId(paymentSuccessEvent.getUserId())
                        .paymentStatus(paymentSuccessEvent.getPaymentStatus())
                        .build());
            }

            case PaymentFailedEvent paymentFailedEvent -> {
                paymentViewRepository.save(PaymentView.builder()
                        .paymentId(paymentFailedEvent.getPaymentId())
                        .paymentMethod(paymentFailedEvent.getPaymentMethod())
                        .price(paymentFailedEvent.getPrice())
                        .orderId(paymentFailedEvent.getOrderId())
                        .timestamp(paymentFailedEvent.getTimestamp())
                        .userId(paymentFailedEvent.getUserId())
                        .paymentStatus(paymentFailedEvent.getPaymentStatus())
                        .build());
            }
            default -> log.warn("Ignore unexcepted message! {}", event);
        }
    }

    @KafkaListener(topics = "saga-topic")
    public void listenSaga(DomainEvent event) {
        log.info("-------------------------------------------");
        log.info("SAGA LISTENING IN PAYMENT EVENT LISTENER");
        log.info("!!!!!Event class: {}", event.getClass().getSimpleName());
        switch (event) {
            case PaymentCreatedEvent paymentCreatedEvent -> {
                try {
                    log.info("-- Received payment created event: {}", paymentCreatedEvent.getClass().getSimpleName());
                    //Change to real payment processing
                    Payment savedPayment = paymentWithExternalAPI(paymentCreatedEvent);
                    log.info("-- Payment created event: {}", savedPayment);
                    paymentEventPublisher.sentPaymentSuccessEvent(savedPayment);

                    updateCache(paymentCreatedEvent, savedPayment);
                    log.info("-- SAGA LISTENING COMPLETE PAYMENT");
                } catch (Exception e) {
                    log.info("-- SAGA LISTENING FAILED PAYMENT {}", e.getMessage());
                    log.error("Failed to create payment {}", e.getMessage());

                    paymentEventPublisher.sentPaymentFailedEvent(paymentCreatedEvent);
                }
            }
            case FinalizeOrderEvent finalizeOrderEvent -> {
                Payment payment = paymentRepository.findById(finalizeOrderEvent.getPaymentId())
                        .orElseThrow(() -> new EntityNotFoundException("Payment not found with id "
                                + finalizeOrderEvent.getPaymentId()));
                payment.setPaymentStatus(PaymentStatus.SUCCESS);
                paymentRepository.save(payment);

                PaymentView paymentView = paymentViewRepository.findById(finalizeOrderEvent.getPaymentId())
                        .orElseThrow(() -> new EntityNotFoundException("Payment not found with id "
                                + finalizeOrderEvent.getPaymentId()));
                paymentView.setPaymentStatus(PaymentStatus.SUCCESS);
                paymentViewRepository.save(paymentView);
            }
            default -> log.warn("Unexpected value: {}", event.getClass().getSimpleName());
        }
    }

    @RateLimiter(name = "paymentRatelimiter", fallbackMethod = "paymentCreateRateLimiterFallback")
    public Payment paymentWithExternalAPI(PaymentCreatedEvent command) {
        Payment payment = Payment.builder()
                .paymentMethod(command.getPaymentMethod())
                .paymentStatus(PaymentStatus.PENDING)
                .timestamp(LocalDateTime.now())
                .price(command.getPrice())
                .orderId(command.getOrderId())
                .userId(command.getUserId())
                .build();
        return paymentRepository.save(payment);
    }

    private void updateCache(PaymentCreatedEvent command, Payment payment) {
        try {
            Objects.requireNonNull(cacheManager.getCache("payments"))
                    .put("payment:" + command.getPaymentId(), payment);
        } catch (Exception e) {
            log.warn("Failed to update payment cache {}", e.getMessage());
        }
    }

    public Payment paymentCreateRateLimiterFallback(PaymentCreatedEvent paymentCreatedEvent, Throwable t) {
        log.error("Failed to create {} because request exceed rate limiter to external API. Cause: {}", paymentCreatedEvent.toString(), t.getMessage(), t);
        throw new CreatingException("Failed to create order:" + paymentCreatedEvent, t);
    }
}
