package by.cryptic.inventoryservice.mapper;

import by.cryptic.inventoryservice.model.Warehouse;
import by.cryptic.inventoryservice.service.command.WarehouseUpdateCommand;
import by.cryptic.utils.DTO.WarehouseDTO;
import org.springframework.stereotype.Component;

@Component
public class WarehouseMapper {

    public static WarehouseDTO toDto(Warehouse warehouse) {
        if (warehouse == null) return null;

        return WarehouseDTO.builder()
                .name(warehouse.getName())
                .longitude(warehouse.getLocation().getX())
                .latitude(warehouse.getLocation().getY())
                .createdAt(warehouse.getCreatedAt())
                .warehouseType(warehouse.getType())
                .build();
    }

    public static void updateEntity(Warehouse warehouse, WarehouseUpdateCommand warehouseUpdateCommand) {
        if (warehouse == null || warehouseUpdateCommand == null) return;

        if (warehouseUpdateCommand.warehouseId() != null) {
            warehouse.setId(warehouseUpdateCommand.warehouseId());
        }
        if (warehouseUpdateCommand.name() != null) {
            warehouse.setName(warehouseUpdateCommand.name());
        }
        if (warehouseUpdateCommand.latitude() != null) {
            warehouse.getLocation().getCoordinate().setY(warehouseUpdateCommand.latitude());
        }
        if (warehouseUpdateCommand.longitude() != null) {
            warehouse.getLocation().getCoordinate().setX(warehouseUpdateCommand.longitude());
        }
        if (warehouseUpdateCommand.warehouseType() != null) {
            warehouse.setType(warehouseUpdateCommand.warehouseType());
        }
        if (warehouseUpdateCommand.active() != null) {
            warehouse.setIsActive(warehouseUpdateCommand.active());
        }
    }
}
