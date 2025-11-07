package by.cryptic.inventoryservice.service.query.handler;

import by.cryptic.inventoryservice.repository.WarehouseRepository;
import by.cryptic.inventoryservice.service.query.WarehouseCheckCapacityQuery;
import by.cryptic.utils.handler.QueryHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseCheckCapacityQueryHandler implements QueryHandler<WarehouseCheckCapacityQuery, Boolean> {

    private final WarehouseRepository warehouseRepository;

    @Override
    public Boolean handle(WarehouseCheckCapacityQuery command) {
        return warehouseRepository.existsWarehouseWithCapacity(command.quantity());
    }
}
