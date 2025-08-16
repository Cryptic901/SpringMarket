package by.cryptic.notificationservice.listener;

import by.cryptic.notificationservice.service.EmailContentBuilder;
import by.cryptic.notificationservice.service.EmailService;
import by.cryptic.utils.event.order.OrderCanceledEvent;
import by.cryptic.utils.event.order.OrderSuccessEvent;
import by.cryptic.utils.event.user.UserLogoutEvent;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationListenerTest {

    @Mock
    private EmailService emailService;

    @Mock
    private EmailContentBuilder emailContentBuilder;

    @InjectMocks
    private NotificationListener notificationListener;

    @Test
    void sendOrderSuccessEvent_shouldSendToEmail() throws MessagingException {
        //Arrange
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        OrderSuccessEvent orderSuccessEvent = OrderSuccessEvent.builder()
                .orderId(orderId)
                .userEmail("user123@gmail.com")
                .price(BigDecimal.valueOf(148.8))
                .listOfProducts(new ArrayList<>())
                .location("Gomel")
                .createdBy(userId)
                .build();
        Mockito.when(emailContentBuilder.buildOrderEmailContent(orderId,
                orderSuccessEvent.getOrderStatus())).thenReturn("mock-content");
        //Act
        notificationListener.sendOrderStatus(orderSuccessEvent);
        //Assert
        verify(emailService, times(1)).sendEmail(eq(orderSuccessEvent.getUserEmail()),
                eq("SpringMarket Order"),
                eq("mock-content"));
        verifyNoMoreInteractions(emailService, emailContentBuilder);
    }

    @Test
    void sendOrderCanceledEvent_shouldSendToEmail() throws MessagingException {
        //Arrange
        UUID orderId = UUID.randomUUID();
        OrderCanceledEvent orderCanceledEvent = OrderCanceledEvent.builder()
                .orderId(orderId)
                .userEmail("user123@gmail.com")
                .build();
        Mockito.when(emailContentBuilder.buildOrderEmailContent(orderId,
                orderCanceledEvent.getOrderStatus())).thenReturn("mock-content");
        //Act
        notificationListener.sendOrderStatus(orderCanceledEvent);
        //Assert
        verify(emailService, times(1)).sendEmail(eq(orderCanceledEvent.getUserEmail()),
                eq("SpringMarket Order"),
                eq("mock-content"));
        verifyNoMoreInteractions(emailService, emailContentBuilder);
    }

    @Test
    void sendRandomEvent_shouldThrowIllegalStateException() {
        //Arrange
        UUID userId = UUID.randomUUID();
        UserLogoutEvent userLogoutEvent = UserLogoutEvent.builder()
                .userId(userId)
                .build();
        //Act
        assertThrows(IllegalStateException.class, () -> notificationListener.sendOrderStatus(userLogoutEvent));
        //Assert
        verifyNoMoreInteractions(emailService, emailContentBuilder);
    }
}