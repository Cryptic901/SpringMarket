package by.cryptic.orderservice.service.command;

import by.cryptic.orderservice.model.write.CustomerOrder;
import by.cryptic.orderservice.publisher.OrderEventPublisher;
import by.cryptic.orderservice.repository.write.CustomerOrderRepository;
import by.cryptic.orderservice.service.command.handler.OrderCancelCommandHandler;
import by.cryptic.utils.enums.OrderStatus;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.GeometryFactory;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class OrderCancelCommandHandlerTest {

    @Mock
    private CustomerOrderRepository orderRepository;

    @Mock
    private OrderEventPublisher orderEventPublisher;

    @Mock
    private GeometryFactory geometryFactory;

    @InjectMocks
    private OrderCancelCommandHandler orderCancelCommandHandler;

    @Test
    void cancelOrder_whenOrderIsCompleted_shouldCancelOrder() {
        //Arrange
        UUID orderId = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        CustomerOrder order = CustomerOrder.builder()
                .id(orderId)
                .userId(userId)
                .price(BigDecimal.valueOf(148.8))
                .orderStatus(OrderStatus.COMPLETED)
                .createdBy(userId)
                .location(geometryFactory.createPoint())
                .paymentId(paymentId)
                .build();
        OrderCancelCommand orderCancelCommand =
                new OrderCancelCommand(orderId, userId, "email@gmail.com");
        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        Mockito.when(orderRepository.save(order)).thenReturn(order);
        //Act
        orderCancelCommandHandler.handle(orderCancelCommand);
        //Assert
        Mockito.verify(orderRepository, Mockito.times(1)).findById(orderId);
        Mockito.verify(orderRepository, Mockito.times(1)).save(order);
        Mockito.verifyNoMoreInteractions(orderRepository);
    }

    @Test
    void cancelOrder_whenOrderIsNotCompleted_shouldCancelOrder() {
        //Arrange
        UUID orderId = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        CustomerOrder order = CustomerOrder.builder()
                .id(orderId)
                .userId(userId)
                .price(BigDecimal.valueOf(148.8))
                .orderStatus(OrderStatus.PENDING)
                .createdBy(userId)
                .location(geometryFactory.createPoint())
                .paymentId(paymentId)
                .build();
        OrderCancelCommand orderCancelCommand =
                new OrderCancelCommand(orderId, userId, "email@gmail.com");
        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        //Act
        Assert.assertThrows(IllegalStateException.class, () -> orderCancelCommandHandler.handle(orderCancelCommand));
        //Assert
        Mockito.verify(orderRepository, Mockito.times(1)).findById(orderId);
    }
}