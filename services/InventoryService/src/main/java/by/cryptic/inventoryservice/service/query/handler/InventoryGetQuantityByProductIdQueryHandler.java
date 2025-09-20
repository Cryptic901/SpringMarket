package by.cryptic.inventoryservice.service.query.handler;

import by.cryptic.inventoryservice.repository.InventoryRepository;
import by.cryptic.inventoryservice.service.query.InventoryGetQuantityByProductIdQuery;
import by.cryptic.utils.handler.QueryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryGetQuantityByProductIdQueryHandler implements QueryHandler<InventoryGetQuantityByProductIdQuery, Integer> {

    private final InventoryRepository inventoryRepository;

    @Override
    public Integer handle(InventoryGetQuantityByProductIdQuery command) {
        return inventoryRepository.getQuantityByProductId(command.productId());
    }
}
