package by.cryptic.inventoryservice.controller.query;

import by.cryptic.inventoryservice.mapper.WarehouseMapper;
import by.cryptic.inventoryservice.model.Inventory;
import by.cryptic.inventoryservice.model.Warehouse;
import by.cryptic.inventoryservice.service.query.WarehouseCheckCapacityQuery;
import by.cryptic.inventoryservice.service.query.WarehouseGetClosestQuery;
import by.cryptic.inventoryservice.service.query.WarehouseGetClosestWithEnoughSpaceQuery;
import by.cryptic.inventoryservice.service.query.handler.WarehouseCheckCapacityQueryHandler;
import by.cryptic.inventoryservice.service.query.handler.WarehouseGetClosestQueryHandler;
import by.cryptic.inventoryservice.service.query.handler.WarehouseGetClosestWithEnoughSpaceQueryHandler;
import by.cryptic.utils.DTO.WarehouseDTO;
import by.cryptic.utils.enums.WarehouseType;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(printOnlyOnFailure = false)
@WebMvcTest(WarehouseQueryController.class)
@ActiveProfiles("mongo")
@Import(WarehouseMapper.class)
class WarehouseQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WarehouseGetClosestQueryHandler warehouseGetClosestQueryHandler;

    @MockitoBean
    private WarehouseGetClosestWithEnoughSpaceQueryHandler warehouseGetClosestWithEnoughSpaceQueryHandler;

    @MockitoBean
    private WarehouseCheckCapacityQueryHandler warehouseCheckCapacityQueryHandler;

    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void getClosestWarehouse_withCorrectData_shouldReturnClosestWarehouse() throws Exception {
        //Arrange
        UUID warehouseId = UUID.randomUUID();
        Warehouse warehouse = Warehouse.builder()
                .id(warehouseId)
                .name("Warehouse")
                .isActive(true)
                .products(new ArrayList<>())
                .type(WarehouseType.MANUFACTURING)
                .location(geometryFactory.createPoint(new Coordinate(21.22, 21.88)))
                .createdAt(LocalDateTime.now())
                .build();
        warehouse.getProducts().add(new Inventory(UUID.randomUUID(), UUID.randomUUID(), warehouse,
                56));
        List<WarehouseDTO> warehouses = new ArrayList<>();
        warehouses.add(WarehouseMapper.toDto(warehouse));
        WarehouseGetClosestQuery warehouseGetClosestQuery = new WarehouseGetClosestQuery(
                55, 21.00, 21.00, 1, UUID.randomUUID()
        );
        String json = objectMapper.writeValueAsString(warehouseGetClosestQuery);
        Mockito.when(warehouseGetClosestQueryHandler.handle(any())).thenReturn(warehouses);
        //Act
        var mvc = mockMvc.perform(get("/api/v1/warehouses/closest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(status().is2xxSuccessful())
                .andExpect(content().json(objectMapper.writeValueAsString(warehouses)));
        //Assert
        String result = mvc.andReturn().getResponse().getContentAsString();
        Assertions.assertEquals(objectMapper.writeValueAsString(warehouses), result);
        Mockito.verify(warehouseGetClosestQueryHandler, Mockito.times(1)).handle(any());
        Mockito.verifyNoMoreInteractions(warehouseGetClosestQueryHandler);
    }

    @Test
    @WithMockUser
    void getClosestWithEnoughSpaceQueryHandler_withCorrectData_shouldReturnClosestWithEnoughSpaceWarehouse() throws Exception {
        //Arrange
        UUID warehouseId = UUID.randomUUID();
        Warehouse warehouse = Warehouse.builder()
                .id(warehouseId)
                .name("Warehouse")
                .isActive(true)
                .products(new ArrayList<>())
                .type(WarehouseType.MANUFACTURING)
                .capacity(1000L)
                .currentLoad(0L)
                .location(geometryFactory.createPoint(new Coordinate(21.22, 21.88)))
                .createdAt(LocalDateTime.now())
                .build();
        warehouse.getProducts().add(new Inventory(UUID.randomUUID(), UUID.randomUUID(), warehouse,
                56));
        WarehouseDTO warehouseDTO = WarehouseMapper.toDto(warehouse);
        WarehouseGetClosestWithEnoughSpaceQuery warehouseGetClosestQuery = new WarehouseGetClosestWithEnoughSpaceQuery(
                55, 21.00, 21.00);
        String json = objectMapper.writeValueAsString(warehouseGetClosestQuery);
        Mockito.when(warehouseGetClosestWithEnoughSpaceQueryHandler.handle(any())).thenReturn(warehouseDTO);
        //Act
        var mvc = mockMvc.perform(get("/api/v1/warehouses/closest/with-enough-product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(status().is2xxSuccessful())
                .andExpect(content().json(objectMapper.writeValueAsString(warehouseDTO)));
        //Assert
        String result = mvc.andReturn().getResponse().getContentAsString();
        Assertions.assertEquals(objectMapper.writeValueAsString(warehouseDTO), result);
        Mockito.verify(warehouseGetClosestWithEnoughSpaceQueryHandler, Mockito.times(1)).handle(any());
        Mockito.verifyNoMoreInteractions(warehouseGetClosestWithEnoughSpaceQueryHandler);
    }

    @Test
    @WithMockUser
    void checkCapacityQueryHandler_withCorrectData_shouldReturnResultOfCheckWarehouse() throws Exception {
        //Arrange
        UUID warehouseId = UUID.randomUUID();
        Warehouse warehouse = Warehouse.builder()
                .id(warehouseId)
                .name("Warehouse")
                .isActive(true)
                .products(new ArrayList<>())
                .type(WarehouseType.MANUFACTURING)
                .capacity(1000L)
                .currentLoad(0L)
                .location(geometryFactory.createPoint(new Coordinate(21.22, 21.88)))
                .createdAt(LocalDateTime.now())
                .build();
        warehouse.getProducts().add(new Inventory(UUID.randomUUID(), UUID.randomUUID(), warehouse,
                56));
        WarehouseCheckCapacityQuery warehouseCheckCapacityQuery = new WarehouseCheckCapacityQuery(
                55);
        String json = objectMapper.writeValueAsString(warehouseCheckCapacityQuery);
        Mockito.when(warehouseCheckCapacityQueryHandler.handle(any())).thenReturn(true);
        //Act
        var mvc = mockMvc.perform(get("/api/v1/warehouses/check-capacity?quantity=" + warehouseCheckCapacityQuery.quantity())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(status().is2xxSuccessful())
                .andExpect(content().string("true"));
        //Assert
        String result = mvc.andReturn().getResponse().getContentAsString();
        Assertions.assertEquals("true", result);
        Mockito.verify(warehouseCheckCapacityQueryHandler, Mockito.times(1)).handle(any());
        Mockito.verifyNoMoreInteractions(warehouseCheckCapacityQueryHandler);
    }
}