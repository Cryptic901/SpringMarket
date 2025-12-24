package by.cryptic.inventoryservice.controller.command;

import by.cryptic.inventoryservice.dto.WarehouseUpdateDTO;
import by.cryptic.inventoryservice.model.Warehouse;
import by.cryptic.inventoryservice.service.command.handler.WarehouseCreateCommandHandler;
import by.cryptic.inventoryservice.service.command.handler.WarehouseDeleteCommandHandler;
import by.cryptic.inventoryservice.service.command.handler.WarehouseUpdateCommandHandler;
import by.cryptic.utils.DTO.WarehouseDTO;
import by.cryptic.utils.enums.WarehouseType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(printOnlyOnFailure = false)
@WebMvcTest(WarehouseCommandController.class)
@ActiveProfiles("jpa")
class WarehouseCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WarehouseCreateCommandHandler warehouseCreateCommandHandler;

    @MockitoBean
    private WarehouseUpdateCommandHandler warehouseUpdateCommandHandler;

    @MockitoBean
    private WarehouseDeleteCommandHandler warehouseDeleteCommandHandler;

    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createReview_withAuthorizedUser_shouldCreateReview() throws Exception {
        //Arrange
        UUID warehouseId = UUID.randomUUID();
        Warehouse warehouse = Warehouse.builder()
                .id(warehouseId)
                .name("name")
                .location(geometryFactory.createPoint(new Coordinate(21.22, 21.42)))
                .isActive(true)
                .type(WarehouseType.FULFILLMENT)
                .capacity(1000L)
                .currentLoad(156L)
                .products(new ArrayList<>())
                .build();
        WarehouseDTO warehouseCreateDTO = new WarehouseDTO(
                warehouse.getName(),
                warehouse.getLocation().getX(),
                warehouse.getLocation().getY(),
                warehouse.getType(),
                warehouse.getCapacity(),
                LocalDateTime.now()
        );
        String json = objectMapper.writeValueAsString(warehouseCreateDTO);
        //Act
        mockMvc.perform(post("/api/v1/warehouses")
                        .with(jwt().jwt(jwt -> jwt.claim("sub", UUID.randomUUID())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
        //Assert
        verify(warehouseCreateCommandHandler, times(1)).handle(any());
        verifyNoMoreInteractions(warehouseCreateCommandHandler);
    }

    @Test
    void createWarehouse_withNotAuthorizedUser_shouldReturnForbidden() throws Exception {
        //Arrange
        UUID warehouseId = UUID.randomUUID();
        Warehouse warehouse = Warehouse.builder()
                .id(warehouseId)
                .name("name")
                .location(geometryFactory.createPoint(new Coordinate(21.22, 21.42)))
                .isActive(true)
                .capacity(1000L)
                .currentLoad(156L)
                .type(WarehouseType.FULFILLMENT)
                .products(new ArrayList<>())
                .build();
        WarehouseDTO warehouseCreateDTO = new WarehouseDTO(
                warehouse.getName(),
                warehouse.getLocation().getX(),
                warehouse.getLocation().getY(),
                warehouse.getType(),
                warehouse.getCapacity(),
                LocalDateTime.now()
        );
        String json = objectMapper.writeValueAsString(warehouseCreateDTO);
        //Act
        mockMvc.perform(post("/api/v1/warehouses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
        //Assert
        verifyNoMoreInteractions(warehouseCreateCommandHandler);
    }

    @Test
    void updateWarehouse_withAuthorizedUser_shouldUpdateWarehouse() throws Exception {
        //Arrange
        UUID reviewId = UUID.randomUUID();
        WarehouseUpdateDTO warehouseUpdateDTO = new WarehouseUpdateDTO
                ("new name", null, null, null, null);
        String json = objectMapper.writeValueAsString(warehouseUpdateDTO);
        //Act
        mockMvc.perform(patch("/api/v1/warehouses/" + reviewId)
                        .with(jwt().jwt(jwt -> jwt.claim("sub", UUID.randomUUID())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is2xxSuccessful());
        //Assert
        verify(warehouseUpdateCommandHandler, times(1)).handle(any());
        verifyNoMoreInteractions(warehouseUpdateCommandHandler);
    }

    @Test
    void updateWarehouse_withNotAuthorizedUser_shouldReturnForbidden() throws Exception {
        //Arrange
        UUID warehouseId = UUID.randomUUID();
        WarehouseUpdateDTO warehouseUpdateDTO = new WarehouseUpdateDTO
                ("new name", null, null, null, null);
        String json = objectMapper.writeValueAsString(warehouseUpdateDTO);
        //Act
        mockMvc.perform(patch("/api/v1/warehouses/" + warehouseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
        //Assert
        verifyNoMoreInteractions(warehouseUpdateCommandHandler);
    }

    @Test
    void deleteWarehouse_withAuthorizedUser_shouldDeleteWarehouse() throws Exception {
        //Arrange
        UUID warehouseId = UUID.randomUUID();
        //Act
        mockMvc.perform(delete("/api/v1/warehouses/" + warehouseId)
                        .with(jwt().jwt(jwt -> jwt.claim("sub", UUID.randomUUID())))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
        //Assert
        verify(warehouseDeleteCommandHandler, times(1)).handle(any());
        verifyNoMoreInteractions(warehouseDeleteCommandHandler);
    }

    @Test
    void deleteWarehouse_withNotAuthorizedUser_shouldReturnForbidden() throws Exception {
        //Arrange
        UUID warehouseId = UUID.randomUUID();
        //Act
        mockMvc.perform(delete("/api/v1/warehouses/" + warehouseId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        //Assert
        verifyNoMoreInteractions(warehouseDeleteCommandHandler);
    }
}