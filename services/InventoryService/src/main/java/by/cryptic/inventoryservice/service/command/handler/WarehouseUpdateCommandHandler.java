package by.cryptic.inventoryservice.service.command.handler;

import by.cryptic.inventoryservice.mapper.WarehouseMapper;
import by.cryptic.inventoryservice.model.Warehouse;
import by.cryptic.inventoryservice.repository.WarehouseRepository;
import by.cryptic.inventoryservice.service.command.WarehouseUpdateCommand;
import by.cryptic.utils.handler.CommandHandler;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseUpdateCommandHandler implements CommandHandler<WarehouseUpdateCommand> {

    private final WarehouseRepository warehouseRepository;

    @Override
    @Transactional
    public void handle(WarehouseUpdateCommand command) {
        Warehouse warehouse = warehouseRepository.findById(command.warehouseId())
                .orElseThrow(() -> new EntityNotFoundException("Warehouse with id: " + command.warehouseId()));
        WarehouseMapper.updateEntity(warehouse, command);
        warehouseRepository.save(warehouse);
    }
}
