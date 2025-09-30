package by.cryptic.orderservice.service.query;

import java.util.UUID;

public record OrderGetByIdQuery(UUID orderId, UUID userId) {
}
