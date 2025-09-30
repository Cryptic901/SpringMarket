package by.cryptic.inventoryservice.publisher;

import by.cryptic.inventoryservice.model.Inventory;
import by.cryptic.inventoryservice.model.OutboxEntity;
import by.cryptic.inventoryservice.model.Reservation;
import by.cryptic.inventoryservice.repository.OutboxRepository;
import by.cryptic.utils.event.EventType;
import by.cryptic.utils.event.product.ProductUpdatedQuantityFromStockEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryEventPublisher {

    private final OutboxRepository outboxRepository;

    public void updateProductQuantity(Inventory inventory) {
        OutboxEntity outbox = OutboxEntity.builder()
                .aggregateId(inventory.getProductId())
                .aggregateType("inventory")
                .eventType(EventType.ProductUpdatedQuantityFromStockEvent.name())
                .payload(ProductUpdatedQuantityFromStockEvent.builder()
                        .productId(inventory.getProductId())
                        .quantity(inventory.getAvailableQuantity())
                        .build())
                .build();
        outboxRepository.save(outbox);
    }
}
