package by.cryptic.productservice.service.command;

import java.util.UUID;

public record ProductDeleteCommand(UUID productId, UUID userId) {
}
