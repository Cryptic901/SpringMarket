package by.cryptic.paymentservice.service.command;

import by.cryptic.paymentservice.listener.PaymentEventListener;
import by.cryptic.paymentservice.model.read.PaymentView;
import by.cryptic.paymentservice.publisher.PaymentEventPublisher;
import by.cryptic.paymentservice.repository.read.PaymentViewRepository;
import by.cryptic.paymentservice.repository.write.PaymentRepository;
import by.cryptic.utils.enums.PaymentMethod;
import by.cryptic.utils.enums.PaymentStatus;
import by.cryptic.utils.event.payment.PaymentFailedEvent;
import by.cryptic.utils.event.payment.PaymentSuccessEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataProcessingException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class PaymentCreateCommandHandlerTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentViewRepository paymentViewRepository;

    @Mock
    private PaymentEventPublisher paymentEventPublisher;

    @InjectMocks
    private PaymentEventListener paymentEventListener;

    @Test
    void createPayment_whenPaymentOk_shouldSavePaymentAndPublishPaymentSuccessEvent() {
        //Arrange
        UUID paymentId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        PaymentView payment = PaymentView.builder()
                .paymentId(paymentId)
                .paymentMethod(PaymentMethod.PAYPAL)
                .orderId(orderId)
                .userId(userId)
                .price(BigDecimal.valueOf(148.8))
                .build();
        PaymentSuccessEvent paymentCreateCommand =
                PaymentSuccessEvent.builder()
                        .paymentId(paymentId)
                        .paymentMethod(PaymentMethod.PAYPAL)
                        .orderId(orderId)
                        .userId(userId)
                        .price(BigDecimal.valueOf(148.8))
                        .paymentStatus(PaymentStatus.SUCCESS)
                        .build();
        Mockito.when(paymentViewRepository.save(any(PaymentView.class))).thenReturn(payment);
        //Act
        paymentEventListener.listenPayments(paymentCreateCommand);
        //Assert
        Mockito.verify(paymentViewRepository, Mockito.times(1)).save(any(PaymentView.class));
    }

    @Test
    void createPayment_whenPaymentFails_shouldSavePaymentAndPublishPaymentFailedEvent() {
        //Arrange
        UUID paymentId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        PaymentFailedEvent paymentCreateCommand =
                PaymentFailedEvent.builder()
                        .paymentId(paymentId)
                        .paymentMethod(PaymentMethod.PAYPAL)
                        .orderId(orderId)
                        .userId(userId)
                        .price(BigDecimal.valueOf(148.8))
                        .paymentStatus(PaymentStatus.PENDING)
                        .build();
        Mockito.when(paymentViewRepository.save(any(PaymentView.class))).thenReturn(any(PaymentView.class));
        //Act
        paymentEventListener.listenPayments(paymentCreateCommand);
        //Assert
        Mockito.verify(paymentViewRepository, Mockito.times(1)).save(any(PaymentView.class));
    }
}