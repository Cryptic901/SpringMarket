package by.cryptic.sagaservice.service;

import by.cryptic.utils.PaymentMethod;
import by.cryptic.utils.PaymentStatus;
import by.cryptic.utils.event.DomainEvent;
import by.cryptic.utils.event.inventory.StockReservationFailedEvent;
import by.cryptic.utils.event.inventory.StockReservedEvent;
import by.cryptic.utils.event.order.OrderCreatedEvent;
import by.cryptic.utils.event.payment.PaymentFailedEvent;
import by.cryptic.utils.event.payment.PaymentSuccessEvent;
import by.cryptic.utils.event.user.UserCreatedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SagaServiceTest {

    @Mock
    private KafkaTemplate<String, DomainEvent> kafkaTemplate;

    @InjectMocks
    private SagaService sagaService;

    @Test
    void processOrderCreatedEvent_shouldProcess() {
        //Arrange
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        OrderCreatedEvent orderCreatedEvent = OrderCreatedEvent.builder()
                .orderId(orderId)
                .userEmail("user123@gmail.com")
                .price(BigDecimal.valueOf(148.8))
                .listOfProducts(new ArrayList<>())
                .location("Gomel")
                .createdBy(userId)
                .build();
        Mockito.when(kafkaTemplate.send(any(), any(), any())).thenReturn(mock());
        //Act
        sagaService.sagaListener(orderCreatedEvent);
        //Assert
        verify(kafkaTemplate, times(1)).send(any(), any(), any());
        verifyNoMoreInteractions(kafkaTemplate);
    }

    @Test
    void processStockReservationFailedEvent_shouldProcess() {
        //Arrange
        UUID orderId = UUID.randomUUID();
        StockReservationFailedEvent event = StockReservationFailedEvent.builder()
                .orderId(orderId)
                .userEmail("user123@gmail.com")
                .build();
        Mockito.when(kafkaTemplate.send(any(), any(), any())).thenReturn(mock());
        //Act
        sagaService.sagaListener(event);
        //Assert
        verify(kafkaTemplate, times(1)).send(any(), any(), any());
        verifyNoMoreInteractions(kafkaTemplate);
    }

    @Test
    void processStockReservedEvent_shouldProcess() {
        //Arrange
        UUID orderId = UUID.randomUUID();
        StockReservedEvent event = StockReservedEvent.builder()
                .orderId(orderId)
                .userEmail("user123@gmail.com")
                .build();
        Mockito.when(kafkaTemplate.send(any(), any(), any())).thenReturn(mock());
        //Act
        sagaService.sagaListener(event);
        //Assert
        verify(kafkaTemplate, times(1)).send(any(), any(), any());
        verifyNoMoreInteractions(kafkaTemplate);
    }



    @Test
    void processPaymentSuccessEvent_shouldProcess() {
        //Arrange
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();
        PaymentSuccessEvent paymentSuccessEvent = PaymentSuccessEvent.builder()
                .paymentId(paymentId)
                .paymentMethod(PaymentMethod.PAYPAL)
                .paymentStatus(PaymentStatus.SUCCESS)
                .orderId(orderId)
                .price(BigDecimal.valueOf(148.8))
                .userId(userId)
                .build();
        Mockito.when(kafkaTemplate.send(any(), any(), any())).thenReturn(mock());
        //Act
        sagaService.sagaListener(paymentSuccessEvent);
        //Assert
        verify(kafkaTemplate, times(1)).send(any(), any(), any());
        verifyNoMoreInteractions(kafkaTemplate);
    }

    @Test
    void processPaymentFailedEvent_shouldProcess() {
        //Arrange
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();
        PaymentFailedEvent paymentFailedEvent = PaymentFailedEvent.builder()
                .paymentId(paymentId)
                .paymentMethod(PaymentMethod.PAYPAL)
                .paymentStatus(PaymentStatus.SUCCESS)
                .orderId(orderId)
                .price(BigDecimal.valueOf(148.8))
                .userId(userId)
                .email("user123@gmail.com")
                .build();
        Mockito.when(kafkaTemplate.send(any(), any(), any())).thenReturn(mock());
        //Act
        sagaService.sagaListener(paymentFailedEvent);
        //Assert
        verify(kafkaTemplate, times(1)).send(any(), any(), any());
        verifyNoMoreInteractions(kafkaTemplate);
    }

    @Test
    void processEvent_shouldThrowIllegalStateException() {
        //Arrange
        UUID userId = UUID.randomUUID();
        UserCreatedEvent userCreatedEvent = UserCreatedEvent.builder()
                .userId(userId)
                .build();
        //Act
        assertThrows(IllegalStateException.class, () -> sagaService.sagaListener(userCreatedEvent));
        //Assert
        verifyNoMoreInteractions(kafkaTemplate);
    }
}