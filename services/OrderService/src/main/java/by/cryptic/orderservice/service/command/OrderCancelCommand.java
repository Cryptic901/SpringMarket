package by.cryptic.orderservice.service.command;

import java.util.UUID;

public record OrderCancelCommand(UUID orderId, UUID userId, String email) {
}
