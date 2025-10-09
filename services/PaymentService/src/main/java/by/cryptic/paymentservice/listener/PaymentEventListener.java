package by.cryptic.paymentservice.listener;

import by.cryptic.paymentservice.model.read.PaymentView;
import by.cryptic.paymentservice.model.write.Payment;
import by.cryptic.paymentservice.publisher.PaymentEventPublisher;
import by.cryptic.paymentservice.repository.read.PaymentViewRepository;
import by.cryptic.paymentservice.repository.write.PaymentRepository;
import by.cryptic.paymentservice.service.ExternalPaymentService;
import by.cryptic.utils.enums.PaymentStatus;
import by.cryptic.utils.event.DomainEvent;
import by.cryptic.utils.event.order.FinalizeOrderEvent;
import by.cryptic.utils.event.payment.PaymentCreatedEvent;
import by.cryptic.utils.event.payment.PaymentFailedEvent;
import by.cryptic.utils.event.payment.PaymentSuccessEvent;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentEventListener {

    private final PaymentViewRepository paymentViewRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentEventPublisher paymentEventPublisher;
    private final CacheManager cacheManager;
    private final ExternalPaymentService externalPaymentService;

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
        log.info("Event class: {}", event.getClass().getSimpleName());
        switch (event) {
            case PaymentCreatedEvent paymentCreatedEvent -> {
                try {
                    Payment savedPayment = externalPaymentService.paymentWithExternalAPI(paymentCreatedEvent);
                    paymentEventPublisher.sentPaymentSuccessEvent(savedPayment);

                    updateCache(paymentCreatedEvent, savedPayment);
                } catch (Exception e) {

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

    private void updateCache(PaymentCreatedEvent command, Payment payment) {
        try {
            Objects.requireNonNull(cacheManager.getCache("payments"))
                    .put("payment:" + command.getPaymentId(), payment);
        } catch (Exception e) {
            log.warn("Failed to update payment cache {}", e.getMessage());
        }
    }
}
