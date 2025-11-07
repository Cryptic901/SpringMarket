package by.cryptic.inventoryservice.service.query.handler;

import by.cryptic.inventoryservice.mapper.WarehouseMapper;
import by.cryptic.inventoryservice.repository.WarehouseRepository;
import by.cryptic.inventoryservice.service.query.WarehouseGetClosestWithEnoughSpaceQuery;
import by.cryptic.utils.DTO.WarehouseDTO;
import by.cryptic.utils.handler.QueryHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseGetClosestWithEnoughSpaceQueryHandler implements QueryHandler<WarehouseGetClosestWithEnoughSpaceQuery, WarehouseDTO> {

    private final WarehouseRepository warehouseRepository;

    @Override
    public WarehouseDTO handle(WarehouseGetClosestWithEnoughSpaceQuery command) {
        return WarehouseMapper.toDto(warehouseRepository
                .findClosestWarehousesWithEnoughSpace(command.lat(), command.lon(), command.quantity()));
    }
}
