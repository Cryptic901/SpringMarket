package by.cryptic.inventoryservice.controller.command;

import by.cryptic.inventoryservice.dto.WarehouseUpdateDTO;
import by.cryptic.inventoryservice.service.command.WarehouseCreateCommand;
import by.cryptic.inventoryservice.service.command.WarehouseDeleteCommand;
import by.cryptic.inventoryservice.service.command.WarehouseUpdateCommand;
import by.cryptic.inventoryservice.service.command.handler.WarehouseCreateCommandHandler;
import by.cryptic.inventoryservice.service.command.handler.WarehouseDeleteCommandHandler;
import by.cryptic.inventoryservice.service.command.handler.WarehouseUpdateCommandHandler;
import by.cryptic.security.JwtUtil;
import by.cryptic.utils.DTO.WarehouseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/warehouses")
@RequiredArgsConstructor
public class WarehouseCommandController {

    private final WarehouseCreateCommandHandler warehouseCreateCommandHandler;
    private final WarehouseDeleteCommandHandler warehouseDeleteCommandHandler;
    private final WarehouseUpdateCommandHandler warehouseUpdateCommandHandler;

    @PostMapping
    @Operation(summary = "Create warehouse", description = "creating warehouse")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Warehouse created"),
            @ApiResponse(responseCode = "400", description = "Request parameters are incorrect"),
            @ApiResponse(responseCode = "403", description = "Request denied (ROLE_ADMIN REQUIRED)"),
            @ApiResponse(responseCode = "503", description = "The creation failed because the server is down")
    })
    public ResponseEntity<Void> createWarehouse(@RequestBody @Valid WarehouseDTO warehouse,
                                                @AuthenticationPrincipal Jwt jwt) {
        warehouseCreateCommandHandler.handle(new WarehouseCreateCommand(
                warehouse.getName(),
                JwtUtil.extractUserId(jwt),
                warehouse.getLatitude(),
                warehouse.getLongitude(),
                warehouse.getWarehouseType(),
                warehouse.getCapacity()
        ));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{warehouseId}")
    @Operation(summary = "Update warehouse", description = "updating warehouse")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Warehouse updated"),
            @ApiResponse(responseCode = "404", description = "Warehouse to update not found"),
            @ApiResponse(responseCode = "403", description = "Request denied (ROLE_ADMIN REQUIRED)"),
            @ApiResponse(responseCode = "503", description = "The updating failed because the server is down")
    })
    public ResponseEntity<Void> updateWarehouse(@RequestBody WarehouseUpdateDTO warehouse,
                                                @PathVariable UUID warehouseId,
                                                @AuthenticationPrincipal Jwt jwt) {
        warehouseUpdateCommandHandler.handle(new WarehouseUpdateCommand(
                warehouseId,
                warehouse.name(),
                warehouse.latitude(),
                warehouse.longitude(),
                warehouse.warehouseType(),
                warehouse.active(),
                JwtUtil.extractUserId(jwt)
        ));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete warehouse", description = "deleting warehouse")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Warehouse deleted"),
            @ApiResponse(responseCode = "404", description = "Warehouse to delete not found"),
            @ApiResponse(responseCode = "403", description = "Request denied (ROLE_ADMIN REQUIRED)"),
            @ApiResponse(responseCode = "503", description = "The deleting failed because the server is down")
    })
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id,
                                              @AuthenticationPrincipal Jwt jwt) {
        warehouseDeleteCommandHandler.handle(new WarehouseDeleteCommand(id, JwtUtil.extractUserId(jwt)));
        return ResponseEntity.noContent().build();
    }
}