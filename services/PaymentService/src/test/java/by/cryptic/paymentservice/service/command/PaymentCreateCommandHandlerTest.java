package by.cryptic.paymentservice.service.command;

import by.cryptic.paymentservice.model.write.Payment;
import by.cryptic.paymentservice.repository.write.PaymentRepository;
import by.cryptic.paymentservice.service.command.handler.PaymentCreateCommandHandler;
import by.cryptic.utils.PaymentMethod;
import by.cryptic.utils.PaymentStatus;
import by.cryptic.utils.event.payment.PaymentCreatedEvent;
import by.cryptic.utils.event.payment.PaymentFailedEvent;
import by.cryptic.utils.event.payment.PaymentSuccessEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataProcessingException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class PaymentCreateCommandHandlerTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private PaymentCreateCommandHandler paymentCreateCommandHandler;

    @Test
    void createPayment_whenPaymentOk_shouldSavePaymentAndPublishPaymentSuccessEvent() {
        //Arrange
        UUID paymentId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Payment payment = Payment.builder()
                .id(paymentId)
                .paymentMethod(PaymentMethod.PAYPAL)
                .orderId(orderId)
                .userId(userId)
                .price(BigDecimal.valueOf(148.8))
                .build();
        PaymentCreatedEvent paymentCreateCommand =
                PaymentCreatedEvent.builder()
                        .paymentId(paymentId)
                        .paymentMethod(PaymentMethod.PAYPAL)
                        .orderId(orderId)
                        .userId(userId)
                        .userEmail("user123@gmail.com")
                        .price(BigDecimal.valueOf(148.8))
                        .paymentStatus(PaymentStatus.PENDING)
                        .build();
        Mockito.when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        //Act
        paymentCreateCommandHandler.handle(paymentCreateCommand);
        //Assert
        Mockito.verify(paymentRepository, Mockito.times(1)).save(any(Payment.class));
        Mockito.verify(applicationEventPublisher, Mockito.times(1))
                .publishEvent(any(PaymentSuccessEvent.class));
    }

    @Test
    void createPayment_whenPaymentFails_shouldSavePaymentAndPublishPaymentFailedEvent() {
        //Arrange
        UUID paymentId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        PaymentCreatedEvent paymentCreateCommand =
                PaymentCreatedEvent.builder()
                        .paymentId(paymentId)
                        .paymentMethod(PaymentMethod.PAYPAL)
                        .orderId(orderId)
                        .userId(userId)
                        .userEmail("user123@gmail.com")
                        .price(BigDecimal.valueOf(148.8))
                        .paymentStatus(PaymentStatus.PENDING)
                        .build();
        Mockito.when(paymentRepository.save(any(Payment.class))).thenThrow(DataProcessingException.class);
        //Act
        paymentCreateCommandHandler.handle(paymentCreateCommand);
        //Assert
        Mockito.verify(paymentRepository, Mockito.times(1)).save(any(Payment.class));
        Mockito.verify(applicationEventPublisher, Mockito.times(1))
                .publishEvent(any(PaymentFailedEvent.class));
    }
}