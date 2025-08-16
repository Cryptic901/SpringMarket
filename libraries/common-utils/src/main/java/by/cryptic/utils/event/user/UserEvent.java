package by.cryptic.utils.event.user;

import java.util.UUID;

@FunctionalInterface
public interface UserEvent {
    UUID getUserId();
}
