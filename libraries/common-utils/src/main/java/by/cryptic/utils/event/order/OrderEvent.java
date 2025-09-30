package by.cryptic.utils.event.order;

import java.util.UUID;

@FunctionalInterface
public interface OrderEvent {
    UUID getOrderId();
}
