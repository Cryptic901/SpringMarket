package by.cryptic.notificationservice.service;

import by.cryptic.utils.OrderStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.UUID;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class EmailContentBuilderTest {

    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private EmailContentBuilder emailContentBuilder;

    @Test
    void buildOrderEmailContent_shouldBuildEmailContent() {
        //Arrange
        UUID orderId = UUID.randomUUID();
        OrderStatus orderStatus = OrderStatus.CANCELLED;
        when(templateEngine.process(any(String.class), any(Context.class))).thenReturn("mock-content");
        //Act
        emailContentBuilder.buildOrderEmailContent(orderId, orderStatus);
        //Assert
        verify(templateEngine, times(1)).process(any(String.class), any(Context.class));
        verifyNoMoreInteractions(templateEngine);
    }
}