package by.cryptic.inventoryservice.service.command.handler;

import by.cryptic.inventoryservice.repository.WarehouseRepository;
import by.cryptic.inventoryservice.service.command.WarehouseDeleteCommand;
import by.cryptic.utils.handler.CommandHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseDeleteCommandHandler implements CommandHandler<WarehouseDeleteCommand> {

    private final WarehouseRepository warehouseRepository;

    @Override
    @Transactional
    public void handle(WarehouseDeleteCommand command) {
        warehouseRepository.deleteById(command.warehouseId());
    }
}
