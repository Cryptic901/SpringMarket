package by.cryptic.cartservice.service.command;

import java.util.UUID;

public record CartDeleteProductCommand(UUID productId, UUID userId) {
}
