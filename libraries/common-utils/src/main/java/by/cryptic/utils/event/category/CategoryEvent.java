package by.cryptic.utils.event.category;

import java.util.UUID;

@FunctionalInterface
public interface CategoryEvent {
    UUID getCategoryId();
}
