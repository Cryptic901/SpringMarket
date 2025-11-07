package by.cryptic.inventoryservice.service.query;

import java.util.UUID;

public record WarehouseGetClosestQuery(Integer quantity,
                                       double lon,
                                       double lat,
                                       Integer limit,
                                       UUID productId) {
}
