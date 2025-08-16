package by.cryptic.inventoryservice.service.command.handler;

import by.cryptic.exceptions.handler.OutOfStockException;
import by.cryptic.inventoryservice.model.Inventory;
import by.cryptic.inventoryservice.repository.InventoryRepository;
import by.cryptic.inventoryservice.service.command.InventoryReserveProductCommand;
import by.cryptic.utils.CommandHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryReserveProductCommandHandler implements CommandHandler<InventoryReserveProductCommand> {

    private final InventoryRepository inventoryRepository;

    public void handle(InventoryReserveProductCommand command) {
        List<Inventory> availableInventories = inventoryRepository.findAvailableByProductId(command.productId());

        if (availableInventories.isEmpty()) {
            throw new OutOfStockException("There are no %s in our stock".formatted(command.productId()));
        }

        for (Inventory inventory : availableInventories) {

        }
    }
}
