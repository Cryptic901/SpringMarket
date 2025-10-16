package by.cryptic.inventoryservice.service.command;

import by.cryptic.utils.enums.WarehouseType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record WarehouseCreateCommand(
        @NotBlank(message = "Name should not be blank")
        String name,
        UUID userId,
        double latitude,
        double longitude,
        @NotBlank(message = "Warehouse type should not be blank")
        WarehouseType warehouseType,
        @NotNull(message = "Warehouse capacity should not be null")
        Long capacity
) {
}
