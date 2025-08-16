package by.cryptic.utils.event.cart;

import java.util.UUID;

@FunctionalInterface
public interface CartEvent {
    UUID getCartId();
}
