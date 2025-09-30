package by.cryptic.utils.event.product;

import java.util.UUID;

@FunctionalInterface
public interface ProductEvent {
    UUID getProductId();
}