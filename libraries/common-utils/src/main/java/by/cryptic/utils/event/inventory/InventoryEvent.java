package by.cryptic.utils.event.inventory;

import java.util.UUID;

@FunctionalInterface
public interface InventoryEvent {
    UUID getInventoryId();
}
