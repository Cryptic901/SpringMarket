package by.cryptic.inventoryservice.service.query.handler;

import by.cryptic.inventoryservice.mapper.WarehouseMapper;
import by.cryptic.inventoryservice.repository.WarehouseRepository;
import by.cryptic.inventoryservice.service.query.WarehouseGetClosestQuery;
import by.cryptic.utils.DTO.WarehouseDTO;
import by.cryptic.utils.handler.QueryHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseGetClosestQueryHandler implements QueryHandler<WarehouseGetClosestQuery, List<WarehouseDTO>> {

    private final WarehouseRepository warehouseRepository;

    @Override
    public List<WarehouseDTO> handle(WarehouseGetClosestQuery command) {
        int resolvedWarehouseLimit = (command.limit() == null || command.limit() < 1) ? 1 : command.limit();
        return warehouseRepository.findClosestWarehousesWithEnoughProducts(command.lat(),
                        command.lon(),
                        resolvedWarehouseLimit, command.quantity(), command.productId()).stream()
                .map(WarehouseMapper::toDto).toList();
    }
}
