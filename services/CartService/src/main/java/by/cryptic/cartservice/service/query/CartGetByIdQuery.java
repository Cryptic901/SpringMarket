package by.cryptic.cartservice.service.query;

import java.util.UUID;

public record CartGetByIdQuery(UUID userId, UUID productId) {
}
