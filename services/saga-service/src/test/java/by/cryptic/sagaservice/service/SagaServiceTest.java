package by.cryptic.sagaservice.service;

import by.cryptic.utils.enums.PaymentMethod;
import by.cryptic.utils.enums.PaymentStatus;
import by.cryptic.utils.event.DomainEvent;
import by.cryptic.utils.event.cart.CartClearedFailedEvent;
import by.cryptic.utils.event.cart.CartClearedSuccessEvent;
import by.cryptic.utils.event.inventory.StockReservationFailedEvent;
import by.cryptic.utils.event.inventory.StockReservedEvent;
import by.cryptic.utils.event.order.OrderCreatedEvent;
import by.cryptic.utils.event.payment.PaymentFailedEvent;
import by.cryptic.utils.event.payment.PaymentSuccessEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.GeometryFactory;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SagaServiceTest {

    @Mock
    private KafkaTemplate<String, DomainEvent> kafkaTemplate;

    @Mock
    private GeometryFactory geometryFactory;

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
                .location(geometryFactory.createPoint())
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
    void processCartClearedSuccessEvent_shouldProcess() {
        //Arrange
        UUID orderId = UUID.randomUUID();
        CartClearedSuccessEvent event = CartClearedSuccessEvent.builder()
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
    void processCartClearedFailedEvent_shouldProcess() {
        //Arrange
        UUID orderId = UUID.randomUUID();
        CartClearedFailedEvent event = CartClearedFailedEvent.builder()
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
                .paymentMethod(PaymentMethod.CARD)
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
                .paymentMethod(PaymentMethod.CARD)
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
}