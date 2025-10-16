package by.cryptic.inventoryservice.service.command;

import java.util.UUID;

public record InventoryReserveProductCommand(UUID productId,
                                             Integer quantity,
                                             UUID orderId,
                                             double lon,
                                             double lat) {
}
