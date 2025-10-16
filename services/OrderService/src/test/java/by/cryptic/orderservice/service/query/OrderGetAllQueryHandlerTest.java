package by.cryptic.orderservice.service.query;

import by.cryptic.orderservice.config.TestBeans;
import by.cryptic.orderservice.dto.OrderDTO;
import by.cryptic.orderservice.mapper.OrderMapper;
import by.cryptic.orderservice.model.read.CustomerOrderView;
import by.cryptic.orderservice.repository.read.OrderViewRepository;
import by.cryptic.orderservice.service.query.handler.OrderGetAllQueryHandler;
import by.cryptic.utils.enums.OrderStatus;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class OrderGetAllQueryHandlerTest {

    @Mock
    private OrderViewRepository orderViewRepository;

    private final GeometryFactory geometryFactory = new GeometryFactory();

    @InjectMocks
    private OrderGetAllQueryHandler orderGetAllQueryHandler;

    @Test
    void getAllOrders_whenOrdersExists_shouldReturnAllOrders() {
        //Arrange
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();
        CustomerOrderView orderView = CustomerOrderView.builder()
                .orderId(orderId)
                .price(BigDecimal.valueOf(148.8))
                .orderStatus(OrderStatus.PENDING)
                .createdBy(userId)
                .location(geometryFactory.createPoint(new Coordinate(21.22, 21.42)))
                .paymentId(paymentId)
                .build();
        OrderDTO orderDTO = OrderMapper.toDto(orderView);
        Mockito.when(orderViewRepository.findAll()).thenReturn(Collections.singletonList(orderView));
        //Act
        List<OrderDTO> result = orderGetAllQueryHandler.handle(new OrderGetAllQuery(userId));
        //Assert
        assertEquals(Collections.singletonList(orderDTO), result);
        Mockito.verify(orderViewRepository, Mockito.times(1)).findAll();
        Mockito.verifyNoMoreInteractions(orderViewRepository);
    }

    @Test
    void getAllOrders_shouldReturnEntityNotFoundException() {
        //Arrange
        Mockito.when(orderViewRepository.findAll()).thenReturn(Collections.emptyList());
        //Act
        //Assert
        assertThrows(EntityNotFoundException.class, () -> orderGetAllQueryHandler.handle(new OrderGetAllQuery(UUID.randomUUID())));
        Mockito.verify(orderViewRepository, Mockito.times(1)).findAll();
        Mockito.verifyNoMoreInteractions(orderViewRepository);
    }
}