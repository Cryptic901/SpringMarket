package by.cryptic.inventoryservice.dto;

import by.cryptic.utils.enums.WarehouseType;

public record WarehouseUpdateDTO(String name, Double latitude, Double longitude,
                                 WarehouseType warehouseType, Boolean active) {
}
