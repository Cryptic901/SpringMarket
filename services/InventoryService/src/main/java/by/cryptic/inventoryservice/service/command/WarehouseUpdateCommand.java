package by.cryptic.inventoryservice.service.command;

import by.cryptic.utils.enums.WarehouseType;

import java.util.UUID;

public record WarehouseUpdateCommand(UUID warehouseId, String name,
                                     Double latitude, Double longitude,
                                     WarehouseType warehouseType, Boolean active, UUID userId) {
}
