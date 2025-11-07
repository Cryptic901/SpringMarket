package by.cryptic.inventoryservice.service.command.handler;

import by.cryptic.inventoryservice.model.Warehouse;
import by.cryptic.inventoryservice.repository.WarehouseRepository;
import by.cryptic.inventoryservice.service.command.WarehouseCreateCommand;
import by.cryptic.utils.handler.CommandHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseCreateCommandHandler implements CommandHandler<WarehouseCreateCommand> {

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    private final WarehouseRepository warehouseRepository;

    @Override
    @Transactional
    public void handle(WarehouseCreateCommand command) {
        Point location = geometryFactory
                .createPoint(new Coordinate(command.longitude(), command.latitude()));
        Warehouse warehouse = Warehouse.builder()
                .type(command.warehouseType())
                .name(command.name())
                .location(location)
                .capacity(command.capacity()) //TODO протестировать логику выгрузки и загрузки склада и сделать коммит
                .currentLoad(0L)
                .isActive(true)
                .build();
        warehouseRepository.save(warehouse);
    }
}
