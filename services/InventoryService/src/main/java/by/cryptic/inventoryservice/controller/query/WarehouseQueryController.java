package by.cryptic.inventoryservice.controller.query;

import by.cryptic.inventoryservice.service.query.WarehouseCheckCapacityQuery;
import by.cryptic.inventoryservice.service.query.WarehouseGetClosestQuery;
import by.cryptic.inventoryservice.service.query.WarehouseGetClosestWithEnoughSpaceQuery;
import by.cryptic.inventoryservice.service.query.handler.WarehouseCheckCapacityQueryHandler;
import by.cryptic.inventoryservice.service.query.handler.WarehouseGetClosestQueryHandler;
import by.cryptic.inventoryservice.service.query.handler.WarehouseGetClosestWithEnoughSpaceQueryHandler;
import by.cryptic.utils.DTO.WarehouseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/warehouses")
@RequiredArgsConstructor
public class WarehouseQueryController {

    private final WarehouseGetClosestQueryHandler warehouseGetClosestQueryHandler;
    private final WarehouseGetClosestWithEnoughSpaceQueryHandler warehouseGetClosestWithEnoughSpaceQueryHandler;
    private final WarehouseCheckCapacityQueryHandler warehouseCheckCapacityQueryHandler;

    @GetMapping("/closest")
    @Operation(summary = "Get closest warehouse", description = "Return closest warehouses with defined limit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Warehouses found"),
            @ApiResponse(responseCode = "404", description = "Warehouses not found"),
    })
    public ResponseEntity<List<WarehouseDTO>> getWarehouseClosest(@RequestBody WarehouseGetClosestQuery command) {
        return ResponseEntity.ok(warehouseGetClosestQueryHandler.handle(command));
    }

    @GetMapping("/closest/with-enough-product")
    @Operation(summary = "Get closest warehouse with enough space", description = "Return closest warehouses with enough space")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Warehouse found"),
            @ApiResponse(responseCode = "404", description = "Warehouse not found"),
    })
    public ResponseEntity<WarehouseDTO> getWarehouseClosest(@RequestBody WarehouseGetClosestWithEnoughSpaceQuery command) {
        return ResponseEntity.ok(warehouseGetClosestWithEnoughSpaceQueryHandler.handle(command));
    }

    @GetMapping("/check-capacity")
    public ResponseEntity<Boolean> checkCapacity(@RequestParam Integer quantity) {
        return ResponseEntity.ok(warehouseCheckCapacityQueryHandler.handle(new WarehouseCheckCapacityQuery(quantity)));
    }
}
