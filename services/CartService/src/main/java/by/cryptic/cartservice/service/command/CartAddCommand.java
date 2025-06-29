package by.cryptic.cartservice.service.command;

import java.util.UUID;

public record CartAddCommand(UUID productId, UUID userId) {
}
