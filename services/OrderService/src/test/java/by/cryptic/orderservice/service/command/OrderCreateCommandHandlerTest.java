package by.cryptic.orderservice.service.command;

import by.cryptic.orderservice.model.write.CustomerOrder;
import by.cryptic.orderservice.repository.write.CustomerOrderRepository;
import by.cryptic.orderservice.service.command.handler.OrderCreateCommandHandler;
import by.cryptic.utils.OrderStatus;
import by.cryptic.utils.event.order.OrderCreatedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.startsWith;

@ExtendWith(MockitoExtension.class)
class OrderCreateCommandHandlerTest {

    @Mock
    private CustomerOrderRepository orderRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private OrderCreateCommandHandler orderCreateCommandHandler;

    @Test
    void createOrder_whenFieldsAreOk_shouldSaveOrder() {
        //Arrange
        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        CustomerOrder order = CustomerOrder.builder()
                .id(orderId)
                .orderStatus(OrderStatus.PENDING)
                .location("gomel")
                .userId(userId)
                .products(new ArrayList<>())
                .price(BigDecimal.ZERO)
                .build();
        OrderCreateCommand orderCreateCommand = new OrderCreateCommand(order.getLocation(),
                userId, "user123@gmail.com");
        Mockito.when(orderRepository.save(any(CustomerOrder.class))).thenReturn(order);
        Mockito.when(cacheManager.getCache("orders")).thenReturn(cache);
        //Act
        orderCreateCommandHandler.handle(orderCreateCommand);
        //Assert
        Mockito.verify(cacheManager).getCache("orders");
        Mockito.verify(cache, Mockito.times(1)).put(startsWith("order:"), any());
        Mockito.verify(orderRepository, Mockito.times(1)).save(any(CustomerOrder.class));
        Mockito.verify(applicationEventPublisher, Mockito.times(1)).publishEvent(any(OrderCreatedEvent.class));
    }
}