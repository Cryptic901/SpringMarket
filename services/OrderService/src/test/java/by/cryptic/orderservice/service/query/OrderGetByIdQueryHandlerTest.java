package by.cryptic.orderservice.service.query;

import by.cryptic.orderservice.dto.OrderDTO;
import by.cryptic.orderservice.mapper.FullOrderMapper;
import by.cryptic.orderservice.model.read.CustomerOrderView;
import by.cryptic.orderservice.repository.read.OrderViewRepository;
import by.cryptic.orderservice.service.query.handler.OrderGetByIdQueryHandler;
import by.cryptic.utils.OrderStatus;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@Transactional
class OrderGetByIdQueryHandlerTest {

    @Mock
    private OrderViewRepository orderRepository;

    @InjectMocks
    private OrderGetByIdQueryHandler orderGetByIdQueryHandler;

    @Test
    void getOrderById_withValidUUID_shouldReturnOrder() {
        //Arrange
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();
        CustomerOrderView orderView = CustomerOrderView.builder()
                .orderId(orderId)
                .price(BigDecimal.valueOf(148.8))
                .orderStatus(OrderStatus.PENDING)
                .createdBy(userId)
                .location("locationtest")
                .paymentId(paymentId)
                .build();
        OrderDTO orderDTO = FullOrderMapper.toDto(orderView);
        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderView));
        //Act
        OrderDTO result = orderGetByIdQueryHandler.handle(new OrderGetByIdQuery(orderId, userId));
        //Assert
        assertEquals(orderDTO, result);
        Mockito.verifyNoMoreInteractions(orderRepository);
    }

    @Test
    void getOrderById_withInvalidUUID_shouldThrowEntityNotFoundException() {
        //Arrange
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        //Act
        //Assert
        assertThrows(EntityNotFoundException.class, () -> orderGetByIdQueryHandler.handle(new OrderGetByIdQuery(orderId, userId)));
        Mockito.verify(orderRepository, Mockito.times(1)).findById(orderId);
        Mockito.verifyNoMoreInteractions(orderRepository);
    }
}