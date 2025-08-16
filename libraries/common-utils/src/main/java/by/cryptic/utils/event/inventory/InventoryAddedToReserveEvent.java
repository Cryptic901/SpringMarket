package by.cryptic.utils.event.inventory;

import by.cryptic.utils.event.DomainEvent;
import by.cryptic.utils.event.category.CategoryCreatedEvent;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryAddedToReserveEvent extends DomainEvent implements InventoryEvent {
    private UUID inventoryId;
    private String sku;
    private UUID orderId;
    private UUID productId;
    private Integer quantityToWriteOff;
    private static final String version = "1.0";
    @Builder.Default
    private String source = CategoryCreatedEvent.class.getName();
}
