package by.cryptic.inventoryservice.service.command;

import java.util.UUID;

public record WarehouseDeleteCommand(UUID warehouseId, UUID userId) {
}
