package by.cryptic.orderservice.controller.command;

import by.cryptic.orderservice.model.write.CustomerOrder;
import by.cryptic.orderservice.service.command.OrderCreateDTO;
import by.cryptic.orderservice.service.command.handler.OrderCancelCommandHandler;
import by.cryptic.orderservice.service.command.handler.OrderCreateCommandHandler;
import by.cryptic.utils.enums.OrderStatus;
import by.cryptic.utils.enums.PaymentMethod;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(printOnlyOnFailure = false)
@WebMvcTest(OrderCommandController.class)
@ActiveProfiles("jpa")
class OrderCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderCreateCommandHandler orderCreateCommandHandler;

    @MockitoBean
    private OrderCancelCommandHandler orderCancelCommandHandler;

    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void createOrder_withAuthorizedUser_shouldCreateOrder() throws Exception {
        //Arrange
        UUID paymentId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        CustomerOrder order = CustomerOrder.builder()
                .id(orderId)
                .userId(userId)
                .price(BigDecimal.valueOf(148.8))
                .orderStatus(OrderStatus.COMPLETED)
                .createdBy(userId)
                .location(geometryFactory.createPoint(new Coordinate(20.22, 21.42)))
                .paymentId(paymentId)
                .build();
        OrderCreateDTO orderCreateDTO = new OrderCreateDTO(order.getLocation().getX(),
                order.getLocation().getY(),
                PaymentMethod.CARD, 5);
        String json = objectMapper.writeValueAsString(orderCreateDTO);
        //Act
        mockMvc.perform(post("/api/v1/orders")
                        .with(jwt().jwt(jwt -> jwt.claim("sub", userId)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
        //Assert
        verify(orderCreateCommandHandler, times(1)).handle(any());
        verifyNoMoreInteractions(orderCreateCommandHandler);
    }

    @Test
    void createOrder_withNotAuthorizedUser_shouldReturnForbidden() throws Exception {
        //Arrange
        UUID paymentId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        CustomerOrder order = CustomerOrder.builder()
                .id(orderId)
                .userId(userId)
                .price(BigDecimal.valueOf(148.8))
                .orderStatus(OrderStatus.COMPLETED)
                .createdBy(userId)
                .location(geometryFactory.createPoint(new Coordinate(20.22, 21.42)))
                .paymentId(paymentId)
                .build();
        OrderCreateDTO orderCreateDTO = new OrderCreateDTO(order.getLocation().getX(),
                order.getLocation().getY(),
                PaymentMethod.CARD, 5);
        String json = objectMapper.writeValueAsString(orderCreateDTO);
        //Act
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
        //Assert
        verifyNoMoreInteractions(orderCreateCommandHandler);
    }

    @Test
    @WithMockUser
    void cancelOrder_withAuthorizedUser_shouldCancelOrder() throws Exception {
        //Arrange
        UUID orderId = UUID.randomUUID();
        //Act
        //Assert
        mockMvc.perform(patch("/api/v1/orders/cancel/{orderId}", orderId)
                        .with(jwt().jwt(jwt -> jwt.claim("sub", UUID.randomUUID())))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @WithMockUser
    void cancelOrder_withNotAuthorizedUser_shouldReturnForbidden() throws Exception {
        //Arrange
        UUID orderId = UUID.randomUUID();
        //Act
        //Assert
        mockMvc.perform(patch("/api/v1/orders/cancel/{orderId}", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}