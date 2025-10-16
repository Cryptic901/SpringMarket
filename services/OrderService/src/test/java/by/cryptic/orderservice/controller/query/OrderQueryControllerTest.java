package by.cryptic.orderservice.controller.query;

import by.cryptic.orderservice.config.TestBeans;
import by.cryptic.orderservice.dto.OrderDTO;
import by.cryptic.orderservice.mapper.OrderMapper;
import by.cryptic.orderservice.model.read.CustomerOrderView;
import by.cryptic.orderservice.service.query.OrderGetByIdQuery;
import by.cryptic.orderservice.service.query.handler.OrderGetAllQueryHandler;
import by.cryptic.orderservice.service.query.handler.OrderGetByIdQueryHandler;
import by.cryptic.utils.enums.OrderStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(printOnlyOnFailure = false)
@WebMvcTest(OrderQueryController.class)
@ActiveProfiles("mongo")
@Import({OrderMapper.class, TestBeans.class})

class OrderQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderGetAllQueryHandler orderGetAllQueryHandler;

    @MockitoBean
    private OrderGetByIdQueryHandler orderGetByIdQueryHandler;

    @Autowired
    private GeometryFactory geometryFactory;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void getAllOrders_withOrdersThatExists_shouldReturnAllOrders() throws Exception {
        //Arrange
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();
        CustomerOrderView order = CustomerOrderView.builder()
                .orderId(orderId)
                .createdBy(userId)
                .price(BigDecimal.valueOf(148.8))
                .orderStatus(OrderStatus.COMPLETED)
                .createdBy(userId)
                .location(geometryFactory.createPoint(new Coordinate(21.22, 21.42)))
                .paymentId(paymentId)
                .build();
        List<OrderDTO> orderDTOS = new ArrayList<>();
        orderDTOS.add(OrderMapper.toDto(order));
        String json = objectMapper.writeValueAsString(orderDTOS);
        Mockito.when(orderGetAllQueryHandler.handle(any())).thenReturn(orderDTOS);
        //Act
        var mvc = mockMvc.perform(get("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwt().jwt(jwt -> jwt.claim("sub", UUID.randomUUID())))
                        .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().json(json));
        //Assert
        String result = mvc.andReturn().getResponse().getContentAsString();
        Assertions.assertEquals(objectMapper.writeValueAsString(orderDTOS), result);
        Mockito.verify(orderGetAllQueryHandler, times(1)).handle(any());
        Mockito.verifyNoMoreInteractions(orderGetAllQueryHandler);
    }

    @Test
    void getAllOrders_withNotAuthorizedUser_shouldReturnUnauthorized() throws Exception {
        //Arrange
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();
        CustomerOrderView order = CustomerOrderView.builder()
                .orderId(orderId)
                .createdBy(userId)
                .price(BigDecimal.valueOf(148.8))
                .orderStatus(OrderStatus.COMPLETED)
                .createdBy(userId)
                .location(geometryFactory.createPoint(new Coordinate(21.22, 21.42)))
                .paymentId(paymentId)
                .build();
        List<OrderDTO> orderDTOS = new ArrayList<>();
        orderDTOS.add(OrderMapper.toDto(order));
        Mockito.when(orderGetAllQueryHandler.handle(any())).thenReturn(orderDTOS);
        //Act
        //Assert
        mockMvc.perform(get("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized());
    }

    @Test
    void getOrderById_withOrderThatExistsAndAuthorizedUser_shouldReturnOrder() throws Exception {
        //Arrange
        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();
        CustomerOrderView order = CustomerOrderView.builder()
                .orderId(orderId)
                .createdBy(userId)
                .price(BigDecimal.valueOf(148.8))
                .orderStatus(OrderStatus.COMPLETED)
                .createdBy(userId)
                .location(geometryFactory.createPoint(new Coordinate(21.22, 21.42)))
                .paymentId(paymentId)
                .build();
        OrderDTO orderDTO = OrderMapper.toDto(order);
        String json = objectMapper.writeValueAsString(orderDTO);
        Mockito.when(orderGetByIdQueryHandler.handle(new OrderGetByIdQuery(orderId, userId))).thenReturn(orderDTO);
        //Act
        mockMvc.perform(get("/api/v1/orders/{orderId}", orderId)
                        .with(jwt().jwt(jwt -> jwt.claim("sub", userId))))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
        //Assert
        Mockito.verify(orderGetByIdQueryHandler, times(1)).handle(new OrderGetByIdQuery(orderId, userId));
        Mockito.verifyNoMoreInteractions(orderGetByIdQueryHandler);
    }

    @Test
    void getOrderById_withOrderThatExistsAndNotAuthorizedUser_shouldReturnUnauthorized() throws Exception {
        //Arrange
        //Act
        //Assert
        mockMvc.perform(get("/api/v1/orders/" + UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }
}