package by.cryptic.inventoryservice.service.query;

public record WarehouseGetClosestWithEnoughSpaceQuery(Integer quantity,
                                                      double lon,
                                                      double lat) {
}
